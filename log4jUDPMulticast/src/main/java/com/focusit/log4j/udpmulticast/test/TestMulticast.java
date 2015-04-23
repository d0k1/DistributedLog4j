package com.focusit.log4j.udpmulticast.test;

import com.focusit.log4j.udpmulticast.threads.NettyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Just an example from netty sources to test if multicast really works in the simplest environment
 * Created by dkirpichenkov on 22.04.15.
 */
public class TestMulticast {

    private static final int port = 9991;
    private static final EventLoopGroup sGroup = new NioEventLoopGroup(3, new NettyThreadFactory("UDPAppender"));
    private static final EventLoopGroup cGroup = new NioEventLoopGroup(3, new NettyThreadFactory("UDPListener"));

    public static void main(String[] args) throws Throwable {
        Bootstrap s = new Bootstrap();
        s.group(sGroup);
        s.channel(NioDatagramChannel.class);

        Bootstrap c = new Bootstrap();
        c.group(cGroup);
        c.channel(NioDatagramChannel.class);

        InetSocketAddress addr = new InetSocketAddress(NetUtil.LOCALHOST4, port);
        s.localAddress(addr);
        c.localAddress(0).remoteAddress(addr);

        testMulticast(s, c);
    }

    public static void testMulticast(Bootstrap sb, Bootstrap cb) throws Throwable {
        MulticastTestHandler mhandler = new MulticastTestHandler();

        sb.handler(new SimpleChannelInboundHandler<Object>() {
            @Override
            public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println("Sc channelRead0");
            }
        });

        cb.handler(mhandler);

        sb.option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF);
        sb.option(ChannelOption.SO_REUSEADDR, true);
        cb.option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF);
        cb.option(ChannelOption.SO_REUSEADDR, true);
        cb.localAddress(port);

        Channel sc = sb.bind().sync().channel();
        DatagramChannel cc = (DatagramChannel) cb.bind().sync().channel();

        String group = "230.0.0.1";
        InetSocketAddress groupAddress = new InetSocketAddress(group, port);

        cc.joinGroup(groupAddress, NetUtil.LOOPBACK_IF).sync();

        sc.writeAndFlush(new DatagramPacket(Unpooled.copyInt(1), groupAddress)).sync();
        if(!mhandler.await()){
            throw new IllegalStateException("no data read!");
        }

        // leave the group
        cc.leaveGroup(groupAddress, NetUtil.LOOPBACK_IF).sync();

        // sleep a second to make sure we left the group
        Thread.sleep(1000);

        // we should not receive a message anymore as we left the group before
        sc.writeAndFlush(new DatagramPacket(Unpooled.copyInt(1), groupAddress)).sync();
        mhandler.await();

        sc.close().awaitUninterruptibly();
        cc.close().awaitUninterruptibly();
    }

    private static final class MulticastTestHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        private final CountDownLatch latch = new CountDownLatch(1);

        private boolean done;
        private volatile boolean fail;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            if (done) {
                fail = true;
            }

            if(msg.content().readInt()!=1) {
                throw new IllegalStateException("Wrong number received");
            }

            latch.countDown();

            // mark the handler as done as we only are supposed to receive one message
            done = true;
        }

        public boolean await() throws Exception {
            boolean success = latch.await(10, TimeUnit.SECONDS);
            if (fail) {
                // fail if we receive an message after we are done
                return false;
            }
            return success;
        }
    }
}
