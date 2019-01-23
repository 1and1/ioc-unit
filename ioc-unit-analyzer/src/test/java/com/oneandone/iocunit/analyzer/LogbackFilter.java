package com.oneandone.iocunit.analyzer;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author aschoerk
 */
public class LogbackFilter extends Filter<ILoggingEvent> {

    private static StringBuffer messages = new StringBuffer();

    private static AtomicBoolean saveMessages = new AtomicBoolean(false);

    public static void clear() {
        synchronized (LogbackFilter.class) {
            messages.setLength(0);
        }
    }

    public static String getMessages() {
        synchronized (LogbackFilter.class) {
            return messages.toString();
        }
    }

    public static void doSaveMessages() {
        saveMessages.set(true);
    }

    public static void endSavingMessages() {
        saveMessages.set(false);
    }

    public static boolean isSavingMessages() {
        return saveMessages.get();
    }


    @Override
    public FilterReply decide(ILoggingEvent event) {
        if(event.getMessage() != null) {
            if(saveMessages.get()) {
                synchronized (LogbackFilter.class) {
                    messages.append(event.getLoggerName() + " - " + event.getFormattedMessage());
                    messages.append("\n");
                }
            }
            return FilterReply.ACCEPT;
        }
        else {
            return FilterReply.NEUTRAL;
        }

    }

}
