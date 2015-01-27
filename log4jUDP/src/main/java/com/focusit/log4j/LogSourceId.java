package com.focusit.log4j;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class LogSourceId {
	private static final LogSourceId instance = new LogSourceId();
	// just ignore jmm side effects
	private String id = null;

	public static LogSourceId getInstance(){
		return instance;
	}

	private LogSourceId(){

	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
}
