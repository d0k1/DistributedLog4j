package com.focusit.log4j.udpmulticast.appender;

import com.focusit.log4j.udpmulticast.LogSourceId;
import com.focusit.log4j.udpmulticast.LoggingEventWrapper;
import com.focusit.log4j.udpmulticast.MulticastSettings;
import com.focusit.log4j.udpmulticast.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.net.*;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPAppender extends AppenderSkeleton {
	private int port = MulticastSettings.getPort();
	Channel ch = null;
	EventLoopGroup group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPAppender"));
	private String mcastAddr = MulticastSettings.getAddress();
	private InetSocketAddress multicastAddress = new InetSocketAddress(mcastAddr, port);

	public UDPAppender() {
	}

	public void activateOptions()  {

		Bootstrap b = new Bootstrap();

		b.group(group)
			.channel(NioDatagramChannel.class);

		b.handler(new Log4jAppenderHandler(port));

		b.option(ChannelOption.IP_MULTICAST_IF, MulticastSettings.getIface());
		b.option(ChannelOption.SO_REUSEADDR, true);
		b.option(ChannelOption.TCP_NODELAY, true);

		InetSocketAddress addr = new InetSocketAddress(MulticastSettings.getAddressToBind(), port);
		b.localAddress(addr);

		try {
			ch = b.bind().sync().channel();
		} catch (InterruptedException e) {
			System.err.println("upd appender error open socket");
		}
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

	public void close() {
		try {
			destroy();
		} catch (InterruptedException e) {
			System.err.println("Error closing udp appender");
		}
	}

	public boolean requiresLayout() {
		return false;
	}
}
