package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Sender extends Thread {

	private UDPLink uLink;
	private boolean running;
	private volatile Queue<Message> sendMessageQueue;
	public ConcurrentHashMap<String, Integer> sentStatus;
	private Queue<Message> sendAckQueue;
	private static List<Message> broadcastList;
	private int latestBcastMsg;
	private int numHosts;

	public Sender(int port, InetAddress address, int numHosts) throws IOException {
		this.uLink = new UDPLink(port, address);
		this.sendMessageQueue = new ConcurrentLinkedQueue();
		this.sendAckQueue = new ConcurrentLinkedQueue<>();
		this.sentStatus = new ConcurrentHashMap<String, Integer>();
		this.broadcastList = new ArrayList();
		this.latestBcastMsg = 0;
		this.numHosts = numHosts;
	}

	public void run() {

        running = true;
        Message messageToSend;
        String key;
        int messageStatus;
        System.out.println("Start Sender Thread");

        while (running) {

        	//System.out.println("Check NORMAL send queue");
 			if (sendMessageQueue.size() > 0) {
 				//System.out.println("There are messages to send");
 				messageToSend = sendMessageQueue.element();
 				key = Integer.toString(messageToSend.getContent()) + "_" + Integer.toString(messageToSend.getDstId());
 				//if status is delivered, remove
 				// else, send it
 				if (sentStatus.containsKey(key)) {
 					//System.out.println("KEY PRESENT");
 					//System.out.println(sentStatus.get(key));
 					if (sentStatus.get(key) == 1) {
 						System.out.println("Sent/got ack, removing:");
 						System.out.println(sentStatus);
 						System.out.println("Removing: " + messageToSend);
 						System.out.println("Old queue: " + sendMessageQueue);
 						sendMessageQueue.remove(messageToSend);
 						System.out.println("New queue: " + sendMessageQueue);
 						System.out.println("Ack queue: " + sendAckQueue);
 					} else {
 						uLink.sendMessage(messageToSend);
 						if ((messageToSend.getDstId() == numHosts) &&
 							(messageToSend.getContent() == latestBcastMsg + 1)) {
 								broadcastList.add(messageToSend);
 								latestBcastMsg = latestBcastMsg + 1;
 							}
 					}
 				} else {
 					System.err.println("Key does not exist?");
 					//uLink.sendMessage(messageToSend);
 				}
 			}

 			//cleanAckQueue(); 

 			//System.out.println("Check ACK send queue");
 			if (sendAckQueue.size() > 0) {
 				//System.out.println("There are messages to send");
 				messageToSend = sendAckQueue.peek();
 				try {
 					//System.out.println("Remove ack");
 					sendAckQueue.remove(messageToSend);
 				}
 				catch (Exception e) {
 					System.out.println("Removing ack error");
 					e.printStackTrace();
 				}
 				uLink.sendMessage(messageToSend);
 			}

 			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

        System.out.println("We stopped running");
    }

    public void sendMessage(Message message) {

    	String key;
    	String msgType = message.getType();
    	//System.out.println("In Sender.sendmsg, Getting message type: " + msgType);
    	key = Integer.toString(message.getContent()) + "_" + Integer.toString(message.getDstId());

    	if (msgType.equals("NORMAL")) {
    		sendMessageQueue.add(message);
    		System.out.println("Added to queue: ");
    		message.printMessage();
    		System.out.println(sendMessageQueue);
    		if (sentStatus.containsKey(key) == false) {
				sentStatus.put(key, 0);
				System.out.println("Added to hashmap: " + sentStatus);
			}
    	} else if (msgType.equals("ACK")) {
    		//System.out.println("Added to ACK queue");
    		//if (sentStatus.containsKey(key)) { 
    		//	if (sentStatus.get(key) == 0) {
    				System.out.println("Added to ack queue: ");
    				message.printMessage();
    				sendAckQueue.add(message);
    		//	}
    		//}
    	}    	
    }

     public static List<Message> getBroadcastList() {
    	return broadcastList;
    }

    public UDPLink getLink() {
    	return uLink;
    }


}