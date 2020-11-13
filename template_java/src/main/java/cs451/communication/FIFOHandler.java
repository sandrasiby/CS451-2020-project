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

// Used as reference to build FIFO implementation: 
// https://pingpong.chalmers.se/public/pp/public_courses/course03499/published/1422395057963/resourceId/1770808/content/Fault-Tolerant%20Broadcast%20-%20Part1.pdf

public class FIFOHandler {

	public ConcurrentHashMap<Integer, Integer> nextSequence;
	public static List<AppMessage> fifoDelivered;
	public static URBHandler urb;

	public FIFOHandler(Sender sender, List<Host> hosts, Host myHost) {

		this.nextSequence = new ConcurrentHashMap<Integer, Integer>(); //initialize to 1
		this.fifoDelivered = new ArrayList<>();
		this.urb = new URBHandler(sender, hosts, myHost);
	}

	public void handleReceivedMessage(Message message) {
		urb.bebDeliverMessage(message);
		fifoDeliver();
	}

	public void fifoDeliver() {

		for (AppMessage m: urb.getDeliveredList()) {

			int messageSender = m.getOriginalSrcId();
			if (nextSequence.containsKey(messageSender) == false ) {
				nextSequence.put(messageSender, 1);
			}

			if (m.getContent() == nextSequence.get(messageSender)) {
				if (fifoDelivered.contains(m) == false) {
					System.out.println("Adding to fifo delivery!");
					fifoDelivered.add(m);
					nextSequence.put(messageSender, m.getContent() + 1);
				}
			}
		}
	}

	public static URBHandler getURBHandler() {
    	return urb;
    }

	public static List<AppMessage> getDeliveredList() {
    	return fifoDelivered;
    }
}