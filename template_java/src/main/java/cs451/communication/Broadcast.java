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

	public Broadcast(Host myHost, String outputFile, List<Host> hosts) {
		
		this.status = 0;
		int numHosts = hosts.size();

		try {
			this.mySender = new Sender(myHost.getPort(), InetAddress.getByName(myHost.getIp()), numHosts);
			this.myReceiver = new Receiver(mySender, outputFile, hosts, myHost);
			this.myReceiver.start();
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.mySender.start();
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