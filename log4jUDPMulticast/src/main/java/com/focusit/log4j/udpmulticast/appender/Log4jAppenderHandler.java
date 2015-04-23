package com.focusit.log4j.udpmulticast.appender;

import com.focusit.log4j.udpmulticast.MulticastSettings;
import com.focusit.log4j.udpmulticast.util.CompactObjectOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;

import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class Log4jAppenderHandler extends ChannelOutboundHandlerAdapter {
	private int port = MulticastSettings.getPort();
	private String mcastAddr = MulticastSettings.getAddress();
	private InetSocketAddress multicastAddress = new InetSocketAddress(mcastAddr, port);

	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	public Log4jAppenderHandler(int port) {
		this.port = port;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof DatagramPacket){
			ctx.write(msg, promise);
			return;
		}

		ByteBuf buf = ctx.alloc().heapBuffer();
//		int startIdx = buf.writerIndex();

		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
//		bout.write(LENGTH_PLACEHOLDER);
		ObjectOutputStream oout = new CompactObjectOutputStream(bout);
		oout.writeObject(msg);
		oout.flush();
		oout.close();
//		int endIdx = buf.writerIndex();

//		buf.setInt(startIdx, endIdx - startIdx - 4);
		Object data = new DatagramPacket(buf, multicastAddress);
		ctx.write(data, promise);
	}
}
