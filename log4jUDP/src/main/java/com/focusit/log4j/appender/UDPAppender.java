package com.focusit.log4j.appender;

import com.focusit.log4j.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPAppender {
	private int port;
	Channel ch = null;
	EventLoopGroup group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPAppender"));

	public UDPAppender(int port) {
		this.port = port;
	}

	public void activateOptions() throws InterruptedException {
		Bootstrap b = new Bootstrap();
		b.group(group)
			.channel(NioDatagramChannel.class)
			.handler(new Log4jAppenderHandler(port))
			.option(ChannelOption.SO_BROADCAST, true);

		ch = b.bind(0).sync().channel();
		ch.pipeline().addLast(new Log4jAppenderHandler(port));
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
}
