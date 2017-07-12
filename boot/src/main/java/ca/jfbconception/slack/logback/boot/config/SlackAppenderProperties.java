package ca.jfbconception.slack.logback.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;

import lombok.Data;

@ConfigurationProperties(prefix = "slack.logback")
@Data
public class SlackAppenderProperties {

    /**
     * Enable custom perspective
     */
    private boolean enabled = true;
    /**
     * Slack webhook url
     */
    
    private String slackHookUrl;
    /**
     * Slack channel
     */
    private String channel;
    /**
     * connection timeout
     */
    private int timeout = 30000;
    /**
     * Treshold filter of the appender, default to error
     */
    private LogLevel treshold = LogLevel.ERROR;

}
