package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.io.Serializable;

/* The AppMessage class is used to represent a message that is delivered at app layer. 
*/

public class AppMessage {

	/*  AppMessage consists of the following:
		msgContent: content of the message (ID from config parameters)
		originalSrcId: the original source process of a message

		Note: Message content acts as a sequence. If content is something else, we
		can just add a new parameter msgSequence. Not adding here to avoid redundancy.
    */

	private int msgContent;
	private int originalSrcId;
	private int[] vectorClock;

	public AppMessage(int msgContent, int originalSrcId, int[] vectorClock) {

		this.msgContent = msgContent;
		this.originalSrcId = originalSrcId;
		this.vectorClock = vectorClock.clone();
	}

	//Function to get message content
	public int getContent() {
		return msgContent;
	}

	//Function to get message's original source
	public int getOriginalSrcId() {
		return originalSrcId;
	}

	public int[] getVectorClock() {
		return vectorClock;
	}


	//Function to print message content as a string
	public String getContentAsString() {
		return Integer.toString(msgContent);
	}

	//Function to print vector clock as a string
	public String getVectorClockAsString() {
		String vc = " | ";
		for (int i = 0; i < vectorClock.length; i++) {
			vc += Integer.toString(vectorClock[i]) + " | ";
		}
		return vc;
	}

	//Function to generate an app layer key
	//We use this key in delivery
	public String getKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId);
	}

	//Function to pretty-print a message
	public void printMessage() {

		System.out.println("****** APP MESSAGE DATA ******");
		System.out.println("Content: " + Integer.toString(msgContent));
		System.out.println("Original Source: " + Integer.toString(originalSrcId));
		System.out.println("Vector Clock: " + getVectorClockAsString());
		System.out.println("**************************");
	}

	//The functions below are overrides to calculate equality of AppMessage objects.
	//We use this throughout the code, when comparing messages.
	//Used this as a reference for overriding: https://www.infoworld.com/article/3305792/comparing-java-objects-with-equals-and-hashcode.html

	@Override
	public boolean equals(Object obj) {

		try {
			if(obj == this) return true; 
			if((obj == null) || (obj.getClass() != this.getClass())) return false; 
			AppMessage m = (AppMessage) obj;
			if ((this.getContent() == m.getContent()) && 
					(this.getOriginalSrcId() == m.getOriginalSrcId())) 
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 1;
	}
}