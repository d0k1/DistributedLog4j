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
import io.netty.util.NetUtil;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.net.*;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPAppender extends AppenderSkeleton {
	private int port = 9991;
	private NetworkInterface iface;
	private String bindOn = "192.168.224.69";
	Channel ch = null;
	EventLoopGroup group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPAppender"));

	public UDPAppender() {
		try {
			iface = NetworkInterface.getByInetAddress(InetAddress.getByName(bindOn));
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void activateOptions()  {

		Bootstrap b = new Bootstrap();

		b.group(group)
			.channel(NioDatagramChannel.class)
			.handler(new Log4jAppenderHandler(port));
		b.option(ChannelOption.SO_REUSEADDR, true);
		b.option(ChannelOption.IP_MULTICAST_IF, iface);
		b.option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, true);
		b.option(ChannelOption.IP_MULTICAST_TTL, 255);

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
