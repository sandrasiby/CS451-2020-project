package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;

public class Message {

	private String msgContent;
	private int dstPort;
	private InetAddress dstAddress;
	private int srcPort;
	private InetAddress srcAddress;
	private String msgType;

	public Message(String msgContent, InetAddress srcAddress, 
		int srcPort, InetAddress dstAddress, int dstPort, String msgType) {
		this.msgContent = msgContent;
		this.srcPort = srcPort;
		this.srcAddress = srcAddress;
		this.dstPort = dstPort;
		this.dstAddress = dstAddress;
		this.msgType = msgType;
	}

	public String getContent() {
		return msgContent;
	}

	public InetAddress getSrcAddress() {
		return srcAddress;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public InetAddress getDstAddress() {
		return dstAddress;
	}

	public int getDstPort() {
		return dstPort;
	}

	public String getType() {
		return msgType;
	}
}