package com.focusit.log4j.udpbroadcast.server;

import com.focusit.log4j.udpbroadcast.LogSourceId;
import com.focusit.log4j.udpbroadcast.LoggingEventWrapper;
import com.focusit.log4j.udpbroadcast.util.CompactObjectInputStream;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.InputStream;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class Log4jHandler extends ChannelHandlerAdapter {
	Logger logger = Logger.getLogger(Log4jHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		DatagramPacket packet = (DatagramPacket) msg;
		try {

			InputStream stream = new ByteBufInputStream(packet.content());
			Object data = new CompactObjectInputStream(stream, ClassResolvers.cacheDisabled(null)).readObject();
			MDC.put("id", LogSourceId.getInstance().getId());
			logger.callAppenders(((LoggingEventWrapper) data).event);

		} catch (Throwable e){
			System.out.println(e);
		}
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("Error "+cause);
		super.exceptionCaught(ctx, cause);
	}
}
