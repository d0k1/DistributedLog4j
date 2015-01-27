package com.focusit.log4j;

import org.apache.log4j.spi.LoggingEvent;

import java.io.Serializable;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class LoggingEventWrapper implements Serializable {
	public final LoggingEvent event;
	public final String id;

	public LoggingEventWrapper(LoggingEvent event, String id) {
		this.event = event;
		this.id = id;
	}
}
