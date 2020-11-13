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

	private int msgContent;
	private int originalSrcId;
	//private int sequenceNumber;
	private int dstPort;
	private InetAddress dstAddress;
	private int srcPort;
	private InetAddress srcAddress;
	private int srcId;
	private int dstId;
	private String msgType;

	public Message(int msgContent, int originalSrcId, InetAddress srcAddress, 
		int srcPort, int srcId, InetAddress dstAddress, int dstPort, int dstId, String msgType) {
		this.msgContent = msgContent;
		this.originalSrcId = originalSrcId;
		this.srcPort = srcPort;
		this.srcAddress = srcAddress;
		this.srcId = srcId;
		this.dstPort = dstPort;
		this.dstAddress = dstAddress;
		this.dstId = dstId;
		this.msgType = msgType;
	}

	public int getContent() {
		return msgContent;
	}

	public int getOriginalSrcId() {
		return originalSrcId;
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
		System.out.println("Content: " + Integer.toString(msgContent));
		System.out.println("Original Source: " + Integer.toString(originalSrcId));
		System.out.println("SrcID: " + Integer.toString(srcId));
		System.out.println("DstID: " + Integer.toString(dstId));
		System.out.println("Type: " + msgType);
		System.out.println("**************************");
	}

	public String getLinkLayerKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId) 
			+ "_" + Integer.toString(dstId);
	}

	public String getLinkLayerAckKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId) 
			+ "_" + Integer.toString(srcId);
	}

	public String getAppLayerKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId);
	}

	public String getContentAsString() {
		return Integer.toString(msgContent);
	}

	//Used this as a reference for overriding: https://www.infoworld.com/article/3305792/comparing-java-objects-with-equals-and-hashcode.html

	@Override
	public boolean equals(Object obj) {

		try {
			if(obj == this) return true; 
			if((obj == null) || (obj.getClass() != this.getClass())) return false; 
			Message m = (Message) obj;
			if ((this.getContent() == m.getContent()) && 
					(this.getOriginalSrcId() == m.getOriginalSrcId()) && 
					(this.getSrcAddress().equals(m.getSrcAddress())) &&
					(this.getSrcPort() == m.getSrcPort()) && 
					(this.getSrcId() == m.getSrcId()) && 
					(this.getDstAddress().equals(m.getDstAddress())) &&
					(this.getDstPort() == m.getDstPort()) && 
					(this.getDstId() == m.getDstId()) &&
					(this.getType().equals(m.getType()))) 
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return srcId;
	}

}