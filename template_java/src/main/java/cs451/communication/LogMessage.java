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

public class LogMessage {

	/*  AppMessage consists of the following:
		msgContent: content of the message (ID from config parameters)
		originalSrcId: the original source process of a message

		Note: Message content acts as a sequence. If content is something else, we
		can just add a new parameter msgSequence. Not adding here to avoid redundancy.
    */

	private int msgContent;
	private int originalSrcId;
	private String type;

	public LogMessage(int msgContent, int originalSrcId, String type) {

		this.msgContent = msgContent;
		this.originalSrcId = originalSrcId;
		this.type = type;
	}

	//Function to pretty-print a message
	public void printMessage() {

		System.out.println("****** LOG MESSAGE DATA ******");
		System.out.println("Content: " + Integer.toString(msgContent));
		System.out.println("Original Source: " + Integer.toString(originalSrcId));
		System.out.println("Type: " + getType());
		System.out.println("**************************");
	}

	//Function to print message content as a string
	public String getContentAsString() {
		return Integer.toString(msgContent);
	}

	//Function to get message's original source
	public int getOriginalSrcId() {
		return originalSrcId;
	}

	public String getType() {
		return type;
	}
}
