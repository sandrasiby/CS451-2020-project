package cs451;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.net.UnknownHostException;
import java.util.concurrent.*; 

/* The Broadcast class is used to set up everything and kick off the broadcast of messages. 
*/

public class Broadcast {

	/*  Broadcast consists of the following:
        mySender: Sender object to handle message sending (thread)
        myReceiver: Receiver object to handle message receiving (thread)
    */

	private Sender mySender;
	private Receiver myReceiver;

	public Broadcast(Host myHost, List<Host> hosts, FileHandler fh) {
		
		int numHosts = hosts.size();

		try {
			this.mySender = new Sender(myHost, numHosts);
			this.myReceiver = new Receiver(mySender, fh, hosts, myHost);
			this.myReceiver.start();
			// try {
			// 	TimeUnit.SECONDS.sleep(1);
			// } catch (InterruptedException e) {
			// 	e.printStackTrace();
			// }
			this.mySender.start();
			System.out.println("Started sender and receiver threads");
			// try {
			// 	TimeUnit.SECONDS.sleep(1);
			// } catch (InterruptedException e) {
			// 	e.printStackTrace();
			// }
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	//Function to perform broadcast. Sends every message to every process
	public boolean sendMessages(int totalMessages, List<Host> hosts, Host myHost) {
 
        Message msgToSend;
    	System.out.println("Process: " + Integer.toString(myHost.getId()));
        for (int m = 1; m < totalMessages + 1; m++) {
            System.out.println("Sending message: " + Integer.toString(m));
            for (Host host: hosts) {
            	System.out.println("Send message to: " + Integer.toString(host.getId()));
            	msgToSend = createMessage(m, myHost, host);
            	mySender.sendMessage(msgToSend);
            }
        }
        return false;
    }

    //Function to create a Message object for sending
    public Message createMessage(int m, Host srcHost, Host dstHost) {

    	try {
	    	int msgContent = m;
	    	int originalSrcId = srcHost.getId();
	    	InetAddress srcAddress = InetAddress.getByName(srcHost.getIp());
	    	int srcPort = srcHost.getPort();
	    	int srcId = srcHost.getId();
	    	InetAddress dstAddress = InetAddress.getByName(dstHost.getIp());
	    	int dstPort = dstHost.getPort();
	    	int dstId = dstHost.getId();
	    	String msgType = "NORMAL";

	    	Message message = new Message(msgContent, originalSrcId, srcAddress, srcPort, srcId,
	    		dstAddress, dstPort, dstId, msgType);
    		return message;
	    } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}