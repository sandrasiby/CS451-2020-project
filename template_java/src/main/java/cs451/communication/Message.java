package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.io.Serializable;

/* The Message class is used to represent a message sent over the links. 
*/

public class Message implements Serializable {

	/*  Message consists of the following:
		msgContent: content of the message (ID from config parameters)
		originalSrcId: the original source process of a message
		msgType: type of the message, whether it is NORMAL or ACK
		srcAddress: IP of the source sending the message
		dstAddress: IP of the destination for a message
		srcPort: Port of the source sending the message
		dstPort: Port of the destination for a message
		srcId: ID of the source sending the message
		dstId: ID of the destination for a message

		Note: Message content acts as a sequence. If content is something else, we
		can just add a new parameter msgSequence. Not adding here to avoid redundancy.
    */

	private int msgContent;
	private int originalSrcId;
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

	//Function to get message content
	public int getContent() {
		return msgContent;
	}

	//Function to get message's original source
	public int getOriginalSrcId() {
		return originalSrcId;
	}

	//Function to get message's sending source IP
	public InetAddress getSrcAddress() {
		return srcAddress;
	}

	//Function to get message's sending source port
	public int getSrcPort() {
		return srcPort;
	}

	//Function to get message's sending source ID
	public int getSrcId() {
		return srcId;
	}

	//Function to get message's destination IP
	public InetAddress getDstAddress() {
		return dstAddress;
	}

	//Function to get message's destination port
	public int getDstPort() {
		return dstPort;
	}

	//Function to get message's destination ID
	public int getDstId() {
		return dstId;
	}

	//Function to get message type
	public String getType() {
		return msgType;
	}

	//Function to pretty-print a message
	public void printMessage() {
		System.out.println("****** MESSAGE DATA ******");
		System.out.println("Content: " + Integer.toString(msgContent));
		System.out.println("Original Source: " + Integer.toString(originalSrcId));
		System.out.println("SrcID: " + Integer.toString(srcId));
		System.out.println("DstID: " + Integer.toString(dstId));
		System.out.println("Type: " + msgType);
		System.out.println("**************************");
	}

	//Function to generate a link layer key
	//We use this key in transportation
	public String getLinkLayerKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId) 
			+ "_" + Integer.toString(dstId);
	}

	//Function to generate a link layer key for ack messages
	//We use this key in transportation
	public String getLinkLayerAckKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId) 
			+ "_" + Integer.toString(srcId);
	}

	//Function to generate an app layer key
	//We use this key in delivery
	public String getAppLayerKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId);
	}

	//Function to print message content as a string (for debugging)
	public String getContentAsString() {
		return Integer.toString(msgContent);
	}

	//The functions below are overrides to calculate equality of Message objects.
	//We use this throughout the code, when comparing messages.
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