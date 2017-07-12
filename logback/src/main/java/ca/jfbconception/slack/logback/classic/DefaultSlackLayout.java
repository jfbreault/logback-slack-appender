package ca.jfbconception.slack.logback.classic;

import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.jfbconception.slack.api.SlackAttachment;
import ca.jfbconception.slack.api.SlackField;
import ca.jfbconception.slack.api.SlackMessage;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LayoutBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DefaultSlackLayout extends LayoutBase<ILoggingEvent> {

    private ObjectMapper objectMapper;
    private String channel;

    private ThrowableProxyConverter throwableProxyConverter;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
    }

    @Override
    public void start() {
        super.start();
        if (throwableProxyConverter == null) {
            throwableProxyConverter = new RootCauseFirstThrowableProxyConverter();
            throwableProxyConverter.setContext(getContext());
            throwableProxyConverter.start();
        }

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
    }

    @Override
    public void stop() {
        throwableProxyConverter.stop();
        super.stop();
    }

    @Override
    public String doLayout(ILoggingEvent event) {

        SlackAttachment.SlackAttachmentBuilder attBiuilder = SlackAttachment.builder() //
                .ts(event.getTimeStamp()) //

                .pretext(levelToEmoji(event.getLevel()) + event.getFormattedMessage()) //
                .color(levelToColor(event.getLevel())) //
                .field(SlackField.builder().title("logger").value(event.getLoggerName()).build());

        addThrowable(event, attBiuilder);
        for (Entry<String, String> entry : event.getMDCPropertyMap().entrySet()) {
            attBiuilder.field(SlackField.builder().title(entry.getKey()).value(entry.getValue()).isShort(true).build());
        }

        SlackMessage message = SlackMessage.builder()//
                .channel(channel) //
                .attachment(attBiuilder.build())//
                .build();//

        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            super.addError(e.getMessage(), e);
            return "";
        }
    }

    protected SlackAttachment.SlackAttachmentBuilder addThrowable(ILoggingEvent event, SlackAttachment.SlackAttachmentBuilder builder) {
        IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable != null) {
            builder //
                    .text(throwableProxyConverter.convert(event))//
                    .field(SlackField.builder().title("message").value(throwable.getMessage()).build()); //
            if (throwable.getCause() != null) {
                builder.field(SlackField.builder().title("cause").value(throwable.getCause().getClassName() + " - " + throwable.getCause().getMessage()).build());
            }
        }

        return builder;
    }

    protected String levelToColor(Level level) {
        switch (level.toInt()) {
        case Level.ERROR_INT:
            return "danger";
        case Level.WARN_INT:
            return "warning";
        default:
            return null;
        }
    }

    protected String levelToEmoji(Level level) {
        switch (level.toInt()) {
        case Level.ERROR_INT:
            return ":bangbang:";
        case Level.WARN_INT:
            return ":warning:";
        case Level.INFO_INT:
            return ":info: ";
        default:
            return null;
        }

    }

}
