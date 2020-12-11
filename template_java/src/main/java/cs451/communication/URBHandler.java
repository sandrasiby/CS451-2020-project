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
import java.net.UnknownHostException;

/* The URBHandler class is used to handle URB operations.
   We use a majority-ACK URB. 
*/

public class URBHandler {

    /*  URBHandler consists of the following:
        sender: Sender object to handle message forwards
        myHost: Process' own details (for forwarding)
        hosts: All process details (for forwarding)
        forwardStatus: HashMap to keep track of message forwarding
        ackURBStatus: HashMap to keep track of acks (for majority ack URB)
        delivered: List of messages that are URB delivered
    */

	public ConcurrentHashMap<String, Integer> forwardStatus;
	public ConcurrentHashMap<String, List<Integer>> ackURBStatus;
	private static List<AppMessage> delivered;
    private static List<AppMessage> pending;
	private Sender sender;
	private Host myHost;
	private List<Host> hosts;

	public URBHandler(Sender sender, List<Host> hosts, Host myHost) {
		this.forwardStatus = new ConcurrentHashMap<String, Integer>();
		this.ackURBStatus = new ConcurrentHashMap<String, List<Integer>>();
		this.sender = sender;
		this.myHost = myHost;
		this.hosts = hosts;
		this.delivered = new ArrayList<>();
        this.pending = new ArrayList<>();
	}

    //Function to update delivery status for a PL delivery, and then perform a URB
	public void bebDeliverMessage(Message message) {
    	String key = message.getAppLayerKey();
    	if (ackURBStatus.containsKey(key) == false) {
    		ackURBStatus.put(key, new ArrayList<Integer>());
    	}
    	List<Integer> receivedIds = ackURBStatus.get(key);
    	if (receivedIds.contains(message.getSrcId()) == false)
    		ackURBStatus.get(key).add(message.getSrcId());
    	forwardMessage(message);
    	AppMessage appMessage = new AppMessage(message.getContent(), 
            message.getOriginalSrcId(), message.getVectorClock());
    	urbDeliver(appMessage);
    }

    //Function to forward a message and update forward status
   	public void forwardMessage(Message message) {

		String key = message.getAppLayerKey();
    	if (forwardStatus.contains(key) == false) {
    		bebBroadcastMessage(message);
    		forwardStatus.put(key, 1);
    	} else {
    		if (forwardStatus.get(key) == 0) {
    			bebBroadcastMessage(message);
    			forwardStatus.put(key, 1);
    		}
    	}
    }

    //Function to forward-broadcast a message
	public void bebBroadcastMessage(Message message) {

		Message msgToSend;

		for (Host host: hosts) {
			if ((host.getId() != message.getSrcId()) && 
				(host.getId() != message.getOriginalSrcId())) {
				msgToSend = createForwardMessage(message, myHost, host);
				sender.sendMessage(msgToSend);
			}
        }
	}

    //Function to check whether a message can be URB delivered (majority ack)
    public boolean canDeliver(AppMessage message) {

    	int numHosts = hosts.size();
    	String key = message.getKey();
    	if ((ackURBStatus.get(key).size() >= numHosts/2) && (delivered.contains(message) == false)) {
    		return true;
    	}
    	return false;
    }

    //Function to URB deliver a message
    public void urbDeliver(AppMessage message) {
    	
        if (canDeliver(message)) {
    		delivered.add(message);
            pending.add(message);
    	}
    }

    //Function to create a Message object for forwarding
 	public Message createForwardMessage(Message oldMessage, Host srcHost, Host dstHost) {

    	try {
	    	int msgContent = oldMessage.getContent();
	    	int originalSrcId = oldMessage.getOriginalSrcId();
	    	InetAddress srcAddress = InetAddress.getByName(srcHost.getIp());
	    	int srcPort = srcHost.getPort();
	    	int srcId = srcHost.getId();
	    	InetAddress dstAddress = InetAddress.getByName(dstHost.getIp());
	    	int dstPort = dstHost.getPort();
	    	int dstId = dstHost.getId();
	    	String msgType = "NORMAL";

	    	Message message = new Message(msgContent, originalSrcId, srcAddress, srcPort, srcId,
	    		dstAddress, dstPort, dstId, msgType, hosts.size());
            message.setVectorClock(oldMessage.getVectorClock());
    		return message;
	    } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Function to get the URB delivered list of messages
    public static List<AppMessage> getDeliveredList() {
    	return delivered;
    }

    public static List<AppMessage> getPendingList() {
        return pending;
    }

    public static void removeFromPendingList(AppMessage m) {
        pending.remove(m);
    }

}