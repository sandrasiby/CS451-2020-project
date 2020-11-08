package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.io.Serializable;

public class Message implements Serializable {

	private String msgContent;
	private int dstPort;
	private InetAddress dstAddress;
	private int srcPort;
	private InetAddress srcAddress;
	private int srcId;
	private int dstId;
	private String msgType;

	public Message(String msgContent, InetAddress srcAddress, 
		int srcPort, int srcId, InetAddress dstAddress, int dstPort, int dstId, String msgType) {
		this.msgContent = msgContent;
		this.srcPort = srcPort;
		this.srcAddress = srcAddress;
		this.srcId = srcId;
		this.dstPort = dstPort;
		this.dstAddress = dstAddress;
		this.dstId = dstId;
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

	public int getSrcId() {
		return srcId;
	}

	public InetAddress getDstAddress() {
		return dstAddress;
	}

	public int getDstPort() {
		return dstPort;
	}

	public int getDstId() {
		return dstId;
	}

	public String getType() {
		return msgType;
	}

	public void printMessage() {
		System.out.println("****** MESSAGE DATA ******");
		System.out.println("Content: " + msgContent);
		System.out.println("SrcID: " + Integer.toString(srcId));
		System.out.println("DstID: " + Integer.toString(dstId));
		System.out.println("Type: " + msgType);
		System.out.println("**************************");
	}
}