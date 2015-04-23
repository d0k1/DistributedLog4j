package com.focusit.log4j.udpmulticast.server;

import com.focusit.log4j.udpmulticast.LogSourceId;
import com.focusit.log4j.udpmulticast.LoggingEventWrapper;
import com.focusit.log4j.udpmulticast.util.CompactObjectInputStream;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.serialization.ClassResolvers;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.InputStream;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class Log4jHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	Logger logger = Logger.getLogger(Log4jHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
		DatagramPacket packet = (DatagramPacket) datagramPacket;
		try {

			InputStream stream = new ByteBufInputStream(packet.content());
			Object data = new CompactObjectInputStream(stream, ClassResolvers.cacheDisabled(null)).readObject();
			MDC.put("id", LogSourceId.getInstance().getId());
			logger.callAppenders(((LoggingEventWrapper) data).event);

		} catch (Throwable e){
			System.out.println(e);
		}
	}
}


