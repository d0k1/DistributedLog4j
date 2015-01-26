package com.focusit.log4j.server;

import com.focusit.log4j.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPListener {

	public String host;
	public int port;

	EventLoopGroup group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPListener"));

	public void activateOptions() throws InterruptedException {
		Bootstrap b = new Bootstrap();
		b.group(group)
			.channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new Log4jHandler());

		b.bind(port).sync();
	}
	public void destroy(){
		group.shutdownGracefully();
	}
}
