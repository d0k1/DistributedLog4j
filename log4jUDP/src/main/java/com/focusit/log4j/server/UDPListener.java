//package com.focusit.log4j.server;
//
//import com.focusit.log4j.threads.NettyThreadFactory;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.DatagramChannel;
//import io.netty.channel.socket.nio.NioDatagramChannel;
//import io.netty.handler.codec.serialization.ClassResolver;
//import io.netty.handler.codec.serialization.ClassResolvers;
//import io.netty.handler.codec.serialization.ObjectDecoder;
//import io.netty.handler.codec.serialization.ObjectEncoder;
//import io.netty.util.NetUtil;
//import org.apache.log4j.Appender;
//import org.apache.log4j.Logger;
//
//import java.net.*;
//import java.nio.channels.SocketChannel;
//import java.util.Map;
//
///**
// * Created by Denis V. Kirpichenkov on 27.01.15.
// */
//public class UDPListener {
//	public int port = 9991;
//
//	private String mcastAddr = "231.7.7.17";
//	private String bindOn = "192.168.224.69";
//	private InetSocketAddress multicastAddress = new InetSocketAddress(mcastAddr, port);
//	private NetworkInterface iface;
//	private DatagramChannel ch;
//
//	EventLoopGroup group;
//
//	public UDPListener(){
//		group = new NioEventLoopGroup(3, new NettyThreadFactory("UDPListener"));
//
//		try {
//			iface = NetworkInterface.getByInetAddress(InetAddress.getByName(bindOn));
//		} catch (SocketException e) {
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void activateOptions() throws InterruptedException {
//
//		Bootstrap b = new Bootstrap();
//		b.group(group)
//			.channel(NioDatagramChannel.class)
//			.handler(new Log4jHandler());
//		b.option(ChannelOption.SO_REUSEADDR, true);
//		b.option(ChannelOption.IP_MULTICAST_IF, iface);
//		b.option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, true);
//		b.option(ChannelOption.IP_MULTICAST_TTL, 255);
////		b.option(ChannelOption.SO_BROADCAST, true);
//		b.localAddress(port);
//		ch = (DatagramChannel) b.bind(0).sync().channel();
//
//		ch.joinGroup(multicastAddress, iface).sync();
//	}
//	public void destroy(){
//		try {
//			ch.leaveGroup(multicastAddress, iface).sync();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		group.shutdownGracefully();
//	}
//}
