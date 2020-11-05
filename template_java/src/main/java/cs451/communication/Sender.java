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

public class Sender extends Thread {

	private UDPLink uLink;
	private boolean running;
	private Queue<Message> sendMessageQueue;
	public volatile ConcurrentHashMap<Message, Integer> sentStatus;
	private Queue<Message> sendAckQueue;

	public Sender(int port, InetAddress address) throws IOException {
		this.uLink = new UDPLink(port, address);
		this.sendMessageQueue = new LinkedList<>();
		this.sendAckQueue = new LinkedList<>();
		this.sentStatus = new ConcurrentHashMap<Message, Integer>();
	}

	public void run() {

        running = true;
        Message messageToSend;
        System.out.println("Start Sender Thread");

        while (running) {
 			if (sendMessageQueue.size() > 0) {
 				messageToSend = sendMessageQueue.element();
 				//if status is delivered, remove
 				// else, send it
 				if (sentStatus.get(messageToSend) == 1) {
 					sendMessageQueue.remove();
 				} else {
 					uLink.sendMessage(messageToSend);
 				}
 			} 

 			if (sendAckQueue.size() > 0) {
 				messageToSend = sendAckQueue.remove();
 				uLink.sendMessage(messageToSend);
 			}
        }
    }

    public void sendMessage(Message message) {

    	String msgType = message.getType();

    	if (msgType == "NORMAL") {
    		sendMessageQueue.add(message);
    		System.out.println("Added to queue: " + message.getContent());
    		if (sentStatus.containsKey(message) == false) {
				sentStatus.put(message, 0);
				System.out.println("Added to hashmap");
			}
    	} else if (msgType == "ACK") {
    		sendAckQueue.add(message);
    	}
    	
    }

    public UDPLink getLink() {
    	return uLink;
    }


}