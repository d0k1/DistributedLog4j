package com.focusit.log4j.server;

import com.focusit.log4j.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPListener {
	public int port = 9991;

	EventLoopGroup group;

	public UDPListener(){
		group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPListener"));
	}

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
