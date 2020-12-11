package cs451;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/* The Receiver class is used to handle message receiving. It runs in a thread.
*/

public class Receiver extends Thread {

	/*  Receiver consists of the following:
        sender: Sender object to coordinate delivery status
        fifo: FIFOHandler to handle message delivery
        fh: FileHandler object to handle writing to file
        running: a boolean value to keep a thread running
        delivered: list to keep messages that were delivered (perfect links) -- not used anymore
        We also add a ShutdownHook to handle shutdown
    */

	private boolean running;
	private static Sender sender;
	private static List<Message> delivered;
	private static FileHandler fh;
	private static LCBHandler lcb;
    public int numHosts;

	public Receiver(Sender sender, FileHandler fh, List<Host> hosts, Host myHost) throws IOException {
		this.sender = sender;
		this.delivered = new ArrayList<>();
		this.fh = fh;
		this.lcb = new LCBHandler(sender, hosts, myHost);
        this.numHosts = hosts.size();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	//Thread running to handle message receiving
	public void run() {

        running = true;
        Message receivedMsg;
        String key;
        String ackContent;
        System.out.println("Start Receiver Thread");
 
        while (running) {

        	receivedMsg = sender.getLink().receiveMessage();
        	if (receivedMsg != null) {
	 			String msgType = receivedMsg.getType();
	 			
	 			if (msgType.equals("NORMAL")) {
	 				//Got message: send ACK, handle delivery process
	 				Message ackMessage = new Message(receivedMsg.getContent(),
	 					receivedMsg.getOriginalSrcId(),
	 					receivedMsg.getDstAddress(), receivedMsg.getDstPort(), receivedMsg.getDstId(),
	 					receivedMsg.getSrcAddress(), receivedMsg.getSrcPort(), 
	 					receivedMsg.getSrcId(), "ACK", numHosts);
                    ackMessage.setVectorClock(receivedMsg.getVectorClock());
	 				sender.sendMessage(ackMessage);
	 				lcb.handleReceivedMessage(receivedMsg);

	 			} else if (msgType.equals("ACK")) {
	 				//Got ack: update sentStatus
	 				key = receivedMsg.getLinkLayerAckKey();
	 				if (sender.sentStatus.containsKey(key)) {
	 					if (sender.sentStatus.get(key) == 0) {
	 						sender.sentStatus.computeIfPresent(key, (k, v) -> new Integer(1));
	 					}
	 				}
	 			}
	        }
	    }
    }


    //Function for perfect links delivery. Not used anymore.
    public void deliverMessagePL(Message message) {
    	
    	if (delivered.contains(message) == false) {
    		//System.out.println("Add to delivery queue: " + message.getContent());
    		delivered.add(message);
    	}
    }

    //Function to get sender
    public static Sender getSender() {
    	return sender;
    }

    //Function to get delivered list of messages. Not used anymore.
    public static List<Message> getDeliveredList() {
    	return delivered;
    }

    //Function to get FileHandler
    public static FileHandler getFileHandler() {
    	return fh;
    }

    //Function to get FIFOHandler
    public static LCBHandler getLCBHandler() {
    	return lcb;
    }
}

