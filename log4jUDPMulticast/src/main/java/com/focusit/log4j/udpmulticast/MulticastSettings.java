package com.focusit.log4j.udpmulticast;

import io.netty.util.NetUtil;

import java.net.*;

/**
 * Multicast settings:
 * Group address
 * Group port
 * IP Address and network interface to bind to
 *
 * Created by Denis V. Kirpichenkov on 24.04.15.
 */
public class MulticastSettings {

	public static int getPort() {
		return 9991;
	}

	public static String getAddress() {
		return "230.0.0.11";
	}

	public static InetAddress getAddressToBind(){
		try {
			return InetAddress.getByName("192.168.1.42");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static NetworkInterface getIface(){

		try {
			return NetworkInterface.getByInetAddress(getAddressToBind());
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return null;
	}
}
