package com.focusit.log4j.main;

import com.focusit.log4j.LogSourceId;
import com.focusit.log4j.appender.UDPAppender;
import com.focusit.log4j.server.UDPListener;
import org.apache.log4j.Logger;
import java.util.Random;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class App {

	public static String getRandomId(){
		Random r = new Random();
		int Low = 10;
		int High = 100;
		int R = r.nextInt(High-Low) + Low;
		return ""+R;
	}

	public static void main(String[] args) throws InterruptedException {
		LogSourceId.getInstance().setId(App.getRandomId());
		Logger logger = Logger.getLogger(App.class);

		StringBuilder builder = new StringBuilder(1024);

		for(int i=0;i<1524;i++){
			builder.append('1');
		}
		System.out.println("SourceId: "+LogSourceId.getInstance().getId());

		UDPListener listener = new UDPListener();
		listener.activateOptions();

		logger.info("Start id: " + LogSourceId.getInstance().getId());
		logger.info(builder.toString());
		logger.info("Stop");
	}
}
