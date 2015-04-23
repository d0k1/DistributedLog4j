package com.focusit.log4j.udpmulticast.server;

import com.focusit.log4j.udpmulticast.MulticastSettings;
import com.focusit.log4j.udpmulticast.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.*;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class UDPListener {
	public int port = MulticastSettings.getPort();

	private String mcastAddr = MulticastSettings.getAddress();
	private InetSocketAddress multicastAddress = new InetSocketAddress(mcastAddr, port);
	private DatagramChannel ch;

	EventLoopGroup group;

	public UDPListener(){
		group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPListener"));
	}

	public void activateOptions() throws InterruptedException {

		Bootstrap b = new Bootstrap();
		b.group(group)
			.channel(NioDatagramChannel.class)
			.handler(new Log4jHandler());
		b.option(ChannelOption.SO_REUSEADDR, true);
		b.option(ChannelOption.IP_MULTICAST_IF, MulticastSettings.getIface());
		b.option(ChannelOption.TCP_NODELAY, true);

		InetSocketAddress addr = new InetSocketAddress(MulticastSettings.getAddressToBind(), port);

		b.localAddress(port).remoteAddress(addr);
		ch = (DatagramChannel) b.bind().sync().channel();

		ch.joinGroup(multicastAddress, MulticastSettings.getIface()).sync();
	}
	public void destroy(){
		try {
			ch.leaveGroup(multicastAddress, MulticastSettings.getIface()).sync();
			ch.close().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		group.shutdownGracefully();
	}
}
