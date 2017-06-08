package ca.jfbconception.slack.logback.boot.config;

import static java.util.Objects.requireNonNull;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;

public class LogbackAppenderRegisterer implements LoggerContextListener, InitializingBean, DisposableBean {

    private final Appender<ILoggingEvent> appender;

    public LogbackAppenderRegisterer(Appender<ILoggingEvent> appender) {
        requireNonNull(appender, "An 'appender' must be set");
        this.appender = appender;
    }

    @Override
    public void afterPropertiesSet() {
        attachAppender();
        attachLoggerContextListener();
    }

    @Override
    public void destroy() {
        removeAppender();
    }

    private void removeAppender() {
        AppenderAttachable<ILoggingEvent> logger = getRootLogger();
        appender.stop();
        logger.detachAppender(appender);
    }

    private void attachAppender() {
        AppenderAttachable<ILoggingEvent> logger = getRootLogger();
        appender.start();
        logger.addAppender(appender);
    }

    private void attachLoggerContextListener() {
        LoggerContext loggerContext = getLoggerContext();
        loggerContext.addListener(this);
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext loggerContext) {
    }

    @Override
    public void onReset(LoggerContext loggerContext) {
        attachAppender();
    }

    @Override
    public void onStop(LoggerContext loggerContext) {
        removeAppender();
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }

    protected LoggerContext getLoggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    protected AppenderAttachable<ILoggingEvent> getRootLogger() {
        return getLoggerContext().getLogger(Logger.ROOT_LOGGER_NAME);
    }

}
