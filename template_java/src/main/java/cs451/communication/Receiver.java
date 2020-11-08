package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class Receiver extends Thread {

	//private UDPLink uLink;
	private boolean running;
	private Sender sender;
	public volatile ConcurrentHashMap<Message, Integer> deliveredStatus;
	private List<Message> delivered;
	private FileHandler fh;

	public Receiver(Sender sender, String outputFile) throws IOException {
		//this.uLink = new UDPLink(port, address);
		this.sender = sender;
		this.deliveredStatus = new ConcurrentHashMap<Message, Integer>();
		this.delivered = new ArrayList<>();
		this.fh = new FileHandler(outputFile);
	}

	public void run() {

        running = true;
        Message receivedMsg;
        String key;
        String ackContent;
        System.out.println("Start Receiver Thread");
 
        while (running) {
        	receivedMsg = sender.getLink().receiveMessage();
 			String msgType = receivedMsg.getType();
 			//System.out.println("Received message: " + msgType);

 			if (msgType.equals("NORMAL")) {
 				//Got message: send ACK
 				//System.out.println("Got normal message!");
 				Message ackMessage = new Message(receivedMsg.getContent(),
 					receivedMsg.getDstAddress(), receivedMsg.getDstPort(), receivedMsg.getDstId(),
 					receivedMsg.getSrcAddress(), receivedMsg.getSrcPort(), 
 					receivedMsg.getSrcId(), "ACK");
 				//System.out.println("Created ack message " + ackMessage);
 				sender.sendMessage(ackMessage);
 				deliverMessage(receivedMsg);
 			} else if (msgType.equals("ACK")) {
 				//Got ack: update sentStatus
 				//System.out.println("Got ACK, update status");
 				key = receivedMsg.getContent() + "_" + Integer.toString(receivedMsg.getSrcId());
 				if (sender.sentStatus.containsKey(key)) {
 					if (sender.sentStatus.get(key) == 0) {
 						sender.sentStatus.computeIfPresent(key, (k, v) -> new Integer(1));
 						//Integer oldStatus = sender.sentStatus.replace(receivedMsg, 1);
 						//System.out.println(sender.sentStatus);
 					}
 				}
 			}
        }
    }

    public void deliverMessage(Message message) {
    	
    	if (delivered.contains(message) == false) {
    		System.out.println("Add to delivery queue: " + message.getContent());
    		delivered.add(message);
    		fh.writeDeliverMessage(message);
    	}
    }
}

