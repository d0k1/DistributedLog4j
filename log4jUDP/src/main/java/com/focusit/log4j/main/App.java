package com.focusit.log4j.main;

import com.focusit.log4j.appender.UDPAppender;
import com.focusit.log4j.server.UDPListener;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class App {

	public static void main(String[] args) throws InterruptedException {
		UDPListener listener = new UDPListener();
		listener.host = "127.0.0.1";
		listener.port = 9991;

		listener.activateOptions();

		UDPAppender appender = new UDPAppender(9991);
		appender.activateOptions();

		appender.sendObject(new Long(123));

		appender.destroy();
		listener.destroy();
	}
}
