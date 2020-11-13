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

public class URBHandler {

	public ConcurrentHashMap<String, Integer> forwardStatus;
	public ConcurrentHashMap<String, List<Integer>> ackURBStatus;
	private static List<AppMessage> delivered;
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
	}

	public void bebDeliverMessage(Message message) {
    	//Update ack
    	System.out.println("In bebdeliver");
    	String key = message.getAppLayerKey();
    	if (ackURBStatus.contains(key) == false) {
    		ackURBStatus.put(key, new ArrayList<Integer>());
    	}
    	List<Integer> receivedIds = ackURBStatus.get(key);
    	if (receivedIds.contains(message.getSrcId()) == false)
    		ackURBStatus.get(key).add(message.getSrcId());
    	forwardMessage(message);
    	AppMessage appMessage = new AppMessage(message.getContent(), message.getOriginalSrcId());
    	urbDeliver(appMessage);
    }

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



    public boolean canDeliver(AppMessage message) {

    	int numHosts = hosts.size();
    	String key = message.getKey();
    	//System.out.println(ackURBStatus.get(key).size());
    	//System.out.println(numHosts/2);
    	if ((ackURBStatus.get(key).size() >= numHosts/2) && (delivered.contains(message) == false)) {
    		return true;
    	}
    	return false;
    }

    public void urbDeliver(AppMessage message) {
    	if (canDeliver(message)) {
    		System.out.println("We're URB delivering here: ");
    		System.out.println(ackURBStatus);
    		delivered.add(message);
    		for (AppMessage m: delivered) {
    			System.out.println(m.getKey());
    		}
    	}
    }

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
	    		dstAddress, dstPort, dstId, msgType);
    		return message;
	    } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<AppMessage> getDeliveredList() {
    	return delivered;
    }

}