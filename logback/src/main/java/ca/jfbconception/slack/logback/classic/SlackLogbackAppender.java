package ca.jfbconception.slack.logback.classic;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jean-Francois Breault
 *
 */
@ToString
@Getter
@Setter
public class SlackLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final int TIMEOUT = 30000;

    private String webHook;
    private String channel;
    private String treshold;
    private URL webHookUrl;
    private int timeout = TIMEOUT;
    private Layout<ILoggingEvent> layout;
    private HttpURLConnectionFactory connectionFactory = new HttpURLConnectionFactory();

    @Override
    public void start() {
        if (webHook != null) {
            try {
                webHookUrl = new URL(webHook);
                super.start();
                if (layout == null) {
                    DefaultSlackLayout layout = new DefaultSlackLayout();
                    layout.setChannel(channel);
                    layout.setContext(this.getContext());
                    layout.start();
                    this.layout = layout;
                }

                if (getCopyOfAttachedFiltersList().isEmpty()) {
                    ThresholdFilter filter = new ThresholdFilter();
                    if (treshold != null) {
                        filter.setLevel(treshold);
                    } else {
                        filter.setLevel(Level.ERROR.toString());
                    }
                    filter.setContext(getContext());
                    filter.start();
                    addFilter(filter);
                }
            } catch (MalformedURLException ex) {
                addError("webHookUrl: '" + webHook + "' is not a valid Url", ex);
            }
        } else {
            addWarn("webHook is mandatory");
        }
    }

    @Override
    protected void append(ILoggingEvent loggingEvent) {
        if (isStarted()) {
            String message = layout.doLayout(loggingEvent);
            try {
                byte[] payload = message.getBytes();

                HttpURLConnection conn = connectionFactory.openConnection(webHookUrl);
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(payload.length);
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStream os = null;
                try {
                    os = conn.getOutputStream();
                    os.write(payload);
                } finally {
                    if (os != null) {
                        os.close();
                    }
                }

            } catch (IOException ex) {
                addError("Error posting log to '" + webHookUrl + "': " + message, ex);
            }
        }
    }

}
