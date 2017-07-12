package ca.jfbconception.slack.logback.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.jfbconception.slack.logback.classic.SlackLogbackAppender;

@Configuration
@EnableConfigurationProperties(SlackAppenderProperties.class)
@ConditionalOnProperty(value = "slack.logback.enabled", matchIfMissing = true)
public class SlackAppenderAutoConfiguration {
    @Autowired
    private SlackAppenderProperties properies;

    @Bean
    public LogbackAppenderRegisterer slackLogbackAppender() {

        SlackLogbackAppender appender = new SlackLogbackAppender();
        appender.setTreshold(properies.getTreshold().toString());
        appender.setWebHook(properies.getSlackHookUrl());
        appender.setChannel(properies.getChannel());
        appender.setTimeout(properies.getTimeout());
        
        return new LogbackAppenderRegisterer(appender);
    }

}
