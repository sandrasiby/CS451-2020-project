package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.net.UnknownHostException;
import java.util.concurrent.*; 

public class Broadcast {

	private int status;
	private Sender mySender;
	private Receiver myReceiver;

	public Broadcast(Host myHost) {
		this.status = 0;

		try {
			this.mySender = new Sender(myHost.getPort(), InetAddress.getByName(myHost.getIp()));
			this.mySender.start();
			this.myReceiver = new Receiver(mySender, myHost.getPort(), InetAddress.getByName(myHost.getIp()));
			this.myReceiver.start();
			System.out.println("Started sender and receiver threads");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public boolean sendMessages(int totalMessages, List<Host> hosts, Host myHost) {
        //For each message
        //For each host
        //Send to all hosts
        Message msgToSend;

    	System.out.println("Process: " + Integer.toString(myHost.getId()));
        for (int m = 1; m < totalMessages + 1; m++) {
            System.out.println("Sending message " + Integer.toString(m));
            for (Host host: hosts) {
            	System.out.println("Send message to: " + Integer.toString(host.getPort()));
            	//Might change format of message. Current format: m_dstID
            	msgToSend = createMessage(m, myHost, host);
            	mySender.sendMessage(msgToSend);
            }
        }
        return false;
    }

    public Message createMessage(int m, Host srcHost, Host dstHost) {

    	try {
	    	String msgContent = Integer.toString(m);
	    	InetAddress srcAddress = InetAddress.getByName(srcHost.getIp());
	    	int srcPort = srcHost.getPort();
	    	InetAddress dstAddress = InetAddress.getByName(dstHost.getIp());
	    	int dstPort = dstHost.getPort();
	    	String msgType = "NORMAL";

	    	Message message = new Message(msgContent, srcAddress, srcPort, dstAddress, dstPort, msgType);
    		return message;

	    } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}