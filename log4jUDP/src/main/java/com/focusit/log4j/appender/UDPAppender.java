package com.focusit.log4j.appender;

import com.focusit.log4j.LogSourceId;
import com.focusit.log4j.LoggingEventWrapper;
import com.focusit.log4j.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPAppender extends AppenderSkeleton {
	private int port = 9991;
	Channel ch = null;
	EventLoopGroup group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPAppender"));

	public UDPAppender() {

	}

	public void activateOptions()  {
		Bootstrap b = new Bootstrap();
		b.group(group)
			.channel(NioDatagramChannel.class)
			.handler(new Log4jAppenderHandler(port))
			.option(ChannelOption.SO_BROADCAST, true);

		try {
			ch = b.bind(0).sync().channel();
		} catch (InterruptedException e) {
			System.err.println("upd appender error open socket");
		}
		ch.pipeline().addLast(new Log4jAppenderHandler(port));
	}

	@Override
	protected void append(LoggingEvent event) {
		try {
			sendObject(new LoggingEventWrapper(event, LogSourceId.getInstance().getId()));
		} catch (InterruptedException e) {
			System.err.println("Error sending event " + event.toString());
		}
	}

	public void sendObject(Object obj) throws InterruptedException {
		ch.writeAndFlush(obj).sync();
	}

	public void destroy() throws InterruptedException {
		try {
			ch.closeFuture().await(500);
		} finally {
			group.shutdownGracefully();
		}
	}

	@Override
	public void close() {
		try {
			destroy();
		} catch (InterruptedException e) {
			System.err.println("Error closing udp appender");
		}
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}
}
