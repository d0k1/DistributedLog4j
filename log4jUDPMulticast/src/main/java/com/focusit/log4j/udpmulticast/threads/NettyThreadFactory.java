package com.focusit.log4j.udpmulticast.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class NettyThreadFactory implements ThreadFactory {

	private final String prefix;
	private final AtomicLong counter = new AtomicLong(0L);

	public NettyThreadFactory(String namePrefix){
		prefix = namePrefix;
	}

	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		thread.setName(prefix+"-"+counter.incrementAndGet());
		return thread;
	}
}
