package cs451;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.lang.*;

/* The Sender class is used to handle message sending. It runs in a thread.
*/

public class Sender extends Thread {

    /*  Sender consists of the following:
        uLink: UDPLink object to send messages as datagrams
        sendMessageQueue: priority queue to which messages are added to be sent (priority decided by comparator)
        sendAckQueue: queue to which ack messages are added to be sent
        window: window that takes in a sub-set of messages from send queues to be sent at a time
        maxWindowSize: window size for normal messages (set to 10)
        ackWindowSize: window size for ack messages (set to 10)
        sentStatus: hashmap to keep track of messages that are received (via acks)
        retransmissionStatus: hashmap to keep track of how many retransmissions occurred for a message
        maxRetransmissions: maximum number of retransmissions allowed (set to 3)
        latestBcastMsg: variable to keep track of the latest message sequence that was broadcast
        broadcastList: list of messages that were broadcast. Used to write to output file.
        running: a boolean value to keep a thread running
    */

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
    private int[] vectorClock;
    private List<Integer> dependencies;
    private static List<LogMessage> allList;

	public Sender(Host myHost, int numHosts, List<Integer> dependencies) throws IOException {
        
        int port = myHost.getPort();
        InetAddress address = InetAddress.getByName(myHost.getIp());
		this.uLink = new UDPLink(port, address);
		this.sendMessageQueue = new PriorityBlockingQueue<>(10, messageComparator);
		this.sendAckQueue = new ConcurrentLinkedQueue<>();
		this.sentStatus = new ConcurrentHashMap<String, Integer>();
        this.retransmissionStatus = new ConcurrentHashMap<String, Integer>();
		this.broadcastList = new ArrayList<>();
        this.allList = new ArrayList<>();
		this.latestBcastMsg = 0;
        this.maxWindowSize = 10;
        this.ackWindowSize = 10;
		this.numHosts = numHosts;
        this.window = new ArrayList<>();
        this.maxRetransmissions = 3;
        this.myHost = myHost;
        this.vectorClock = new int[numHosts];
        this.dependencies = dependencies;
	}

    //Thread running to handle message sending
	public void run() {

        running = true;
        Message messageToSend;
        String key;
        int messageStatus;
        int windowSize;
        System.out.println("Start Sender Thread");

        while (running) {

            //We check the status of sent messages in window, to see what needs retransmission
            for (Message m: window) {
                key = m.getLinkLayerKey();
                if (sentStatus.get(key) == 0) {
                    if (retransmissionStatus.containsKey(key) == false) {
                        retransmissionStatus.put(key, 0);
                    }
                    retransmissionStatus.computeIfPresent(key, (k, v) -> v + 1);
                    if (retransmissionStatus.get(key) <= maxRetransmissions) {
                        sendMessageQueue.add(m);
                    }
                }
            }

            //Clear window to add new messages
            window.clear();

            //We check the main queue for things to be sent, and add to window
            if (sendMessageQueue.size() > 0) {  
                
                windowSize = Math.min(maxWindowSize, sendMessageQueue.size());
                for (int i = 0; i < windowSize; i++) {
                    Message m = sendMessageQueue.poll();
                    window.add(m);
                }

                //Send each message in the window
                for (Message m: window) {
                    if (m.getOriginalSrcId() == myHost.getId()) {
                        //Set vector clock only for own messages and first time
                        if (m.getAge() == 0) {
                            int[] newVectorClock = setMessageVectorClock(m);
                            m.setVectorClock(newVectorClock);
                            //System.out.println("Sending out message");
                            //m.printMessage();
                            //System.out.println("Process clock");
                            //printVectorClock(vectorClock);
                        }
                    }
                    m.updateAge();
                    messageStatus = uLink.sendMessage(m);
                    if ((m.getDstId() == numHosts) &&
                         (m.getSrcId() == myHost.getId()) &&
                         (m.getContent() == latestBcastMsg + 1)) {
                    //if ((m.getSrcId() == myHost.getId()) && 
                    //    (m.getContent() == latestBcastMsg + 1)) {
                            broadcastList.add(m);
                            LogMessage lm = new LogMessage(m.getContent(), m.getOriginalSrcId(), "b");
                            allList.add(lm);
                            latestBcastMsg = latestBcastMsg + 1;
                            updateVectorClock(myHost.getId()-1);
                    }
                }
            }
 
            //Send acks in ack queue, one window at a time
 			if (sendAckQueue.size() > 0) {
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
        }

        System.out.println("We stopped running");
    }

    //Function to add messages to the send queue. 
    //Also adds an entry for a message in the delivery and retransmission status hashmaps
    public void sendMessage(Message message) {

    	String key;
    	String msgType = message.getType();
    	
        key = message.getLinkLayerKey();

    	if (msgType.equals("NORMAL")) {
    		sendMessageQueue.add(message);
    		if (sentStatus.containsKey(key) == false) {
				sentStatus.put(key, 0);
			}
            if (retransmissionStatus.containsKey(key) == false) {
                retransmissionStatus.put(key, 0);
            }
    	} else if (msgType.equals("ACK")) {
    		sendAckQueue.add(message);
    	}    	
    }

    public int[] getProcessVectorClock() {
        return vectorClock;
    }

    public void updateVectorClock(int indexToUpdate) {
        vectorClock[indexToUpdate] += 1;
    }

    public void updateAllList(LogMessage lm) {
        allList.add(lm);
    }

    //Function to get the list of broadcast messages
    public static List<Message> getBroadcastList() {
    	return broadcastList;
    }

    //Function to get the list of broadcast messages
    public static List<LogMessage> getAllList() {
        return allList;
    }

    //Function to get the UDP link for datagram exchange
    public UDPLink getLink() {
    	return uLink;
    }

    public int[] setMessageVectorClock(Message m) {

        int[] newMessageVectorClock = m.getVectorClock();

        for (int i = 0; i < newMessageVectorClock.length; i++) {
            if (dependencies.contains(i+1)) {
                int value = vectorClock[i];
                newMessageVectorClock[i] = value;
            } else {
                newMessageVectorClock[i] = 0;
            }
        }

        return newMessageVectorClock;
    }

    public void printVectorClock(int[] vectorClock) {
        String vc = " | ";
        for (int i = 0; i < vectorClock.length; i++) {
            vc += Integer.toString(vectorClock[i]) + " | ";
        }
        System.out.println(vc);
    }

    //Function to calculate priority of messages for priority queue (description in comparator)
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

    //Overriding priority queue's comparator function to generate own own set of priorities
    //Used this as a reference on how to override: 
    //https://www.geeksforgeeks.org/implement-priorityqueue-comparator-java/

    public Comparator<Message> messageComparator = new Comparator<Message>(){
        @Override
        public int compare(Message m1, Message m2) {
            //We calcuate a priority paramater based on the following assumptions:
            //Lower sequences are more important than higher sequences
            //A host's own message is more important than a forward
            //First transmission is more important than subsequent re-transmissions.
            //Each subsequent retransmission goes lower in importance (similar idea to an exponential back-off)
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