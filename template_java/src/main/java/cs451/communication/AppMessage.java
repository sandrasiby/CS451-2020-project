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

	public AppMessage(int msgContent, int originalSrcId) {

		this.msgContent = msgContent;
		this.originalSrcId = originalSrcId;
	}

	//Function to get message content
	public int getContent() {
		return msgContent;
	}

	//Function to get message's original source
	public int getOriginalSrcId() {
		return originalSrcId;
	}

	//Function to print message content as a string
	public String getContentAsString() {
		return Integer.toString(msgContent);
	}

	//Function to generate an app layer key
	//We use this key in delivery
	public String getKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId);
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