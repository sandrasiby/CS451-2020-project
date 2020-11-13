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
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.lang.*;

public class Sender extends Thread {

	private UDPLink uLink;
	private boolean running;
	private volatile Queue<Message> sendMessageQueue;
	public ConcurrentHashMap<String, Integer> sentStatus;
    public ConcurrentHashMap<String, Integer> retransmissionStatus;
	private Queue<Message> sendAckQueue;
	private static List<Message> broadcastList;
    private List<Message> window;
	private int latestBcastMsg;
	private int numHosts;
    private int maxWindowSize;
    private int maxRetransmissions;
    private Host myHost;
    private int ackWindowSize;

	public Sender(Host myHost, int numHosts) throws IOException {
        
        int port = myHost.getPort();
        InetAddress address = InetAddress.getByName(myHost.getIp());
		this.uLink = new UDPLink(port, address);
		this.sendMessageQueue = new PriorityQueue<>(10, messageComparator);
		this.sendAckQueue = new ConcurrentLinkedQueue<>();
		this.sentStatus = new ConcurrentHashMap<String, Integer>();
        this.retransmissionStatus = new ConcurrentHashMap<String, Integer>();
		this.broadcastList = new ArrayList<>();
		this.latestBcastMsg = 0;
        this.maxWindowSize = 10;
        this.ackWindowSize = 10;
		this.numHosts = numHosts;
        this.window = new ArrayList<>();
        this.maxRetransmissions = 3;
        this.myHost = myHost;
	}

	public void run() {

        running = true;
        Message messageToSend;
        String key;
        int messageStatus;
        int windowSize;
        System.out.println("Start Sender Thread");

        while (running) {

            //We check the status of sent messages, to see what needs retransmission
            for (Message m: window) {
                key = m.getLinkLayerKey();
                if (sentStatus.get(key) == 0) {
                    retransmissionStatus.computeIfPresent(key, (k, v) -> v + 1);
                    if (retransmissionStatus.get(key) <= maxRetransmissions) {
                        sendMessageQueue.add(m);
                    } else {
                        System.out.println("We exceeded max retransmission!");
                    }
                }
            }

            window.clear();

            //We check the main queue for things to be sent, and add to window
            if (sendMessageQueue.size() > 0) {  
                
                windowSize = Math.min(maxWindowSize, sendMessageQueue.size());
                for (int i = 0; i < windowSize; i++) {
                    Message m = sendMessageQueue.poll();
                    window.add(m);
                }

                for (Message m: window) {
                    messageStatus = uLink.sendMessage(m);
                    if ((m.getDstId() == numHosts) &&
                        (m.getSrcId() == myHost.getId()) &&
                        (m.getContent() == latestBcastMsg + 1)) {
                            broadcastList.add(m);
                            latestBcastMsg = latestBcastMsg + 1;
                    }
                }
            }
 
 			//System.out.println("Check ACK send queue");
 			if (sendAckQueue.size() > 0) {
 				//System.out.println("There are messages to send");
                int counter = 0;
                windowSize = Math.min(ackWindowSize, sendAckQueue.size());
                while (counter < windowSize) {
                    messageToSend = sendAckQueue.peek();
                    try {
                        sendAckQueue.remove(messageToSend);
                    } catch (Exception e) {
                        System.out.println("Removing ack error");
                        e.printStackTrace();
                    }
                    uLink.sendMessage(messageToSend);
                    counter++;
                }
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
    	//key = Integer.toString(message.getContent()) + "_" + Integer.toString(message.getDstId());
        key = message.getLinkLayerKey();

    	if (msgType.equals("NORMAL")) {
    		sendMessageQueue.add(message);
    		System.out.println("Added to queue: ");
    		message.printMessage();
            // System.out.println("Messages in queue are:");
            // for (Message m: sendMessageQueue) {
            //     System.out.println(message.getLinkLayerKey());
            // }
    		//System.out.println(sendMessageQueue);
    		if (sentStatus.containsKey(key) == false) {
				sentStatus.put(key, 0);
				System.out.println("Added to hashmap: " + sentStatus);
			}
            if (retransmissionStatus.containsKey(key) == false) {
                retransmissionStatus.put(key, 0);
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

    public double getPriority(Message m) {
        
        int s = m.getContent();
        int h;
        int r;

        if (m.getOriginalSrcId() == myHost.getId()) 
            h = 1;
        else
            h = 2;

        String key = m.getLinkLayerKey();
        if (retransmissionStatus.containsKey(key) == false)
            r = 0;
        else
            r = retransmissionStatus.get(key);

        return 1/(s * h * Math.pow(r + 1, 2));
    }

    public Comparator<Message> messageComparator = new Comparator<Message>(){
        @Override
        public int compare(Message m1, Message m2) {
            //We calcuate a priority paramater based on the following assumptions:
            //Lower sequences are more important than higher sequences
            //A host's own message is more important than a forward
            //First transmission is more important than subsequent re-transmissions.
            //Each subsequent retransmission goes lower in importance (similar to an exponential back-off)
            //Based on this: we calculate priority P as:
            //P = 1/(s * h * (r+1)^2)
            // s = sequence number, h = sender (1 for own message, 2 for forwards), r = retransmission status
            double p1 = getPriority(m1);
            double p2 = getPriority(m2);

            if (p1 < p2)
                return 1;
            else if (p1 > p2)
                return -1;
            return 0;
        }
    };


}