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

/* The FIFOHandler class is used to handle FIFO delivery of messages. 
*/

public class FIFOHandler {

	/*  FIFOHandler consists of the following:
		urb: URB Handler that handles URB delivery/broadcast and gets messages for delivery
		fifoDelivered: List of messages in FIFO ordered. Used as input for the FileHandler
		to write into output file.
		nextSequence: Hashmap to keep track of sequence for a process' messages

		Note: Message content acts as the sequence for messages. 
    */

	public ConcurrentHashMap<Integer, Integer> nextSequence;
	public static List<AppMessage> fifoDelivered;
	public static URBHandler urb;

	public FIFOHandler(Sender sender, List<Host> hosts, Host myHost) {

		this.nextSequence = new ConcurrentHashMap<Integer, Integer>(); //initialize to 1
		this.fifoDelivered = new ArrayList<>();
		this.urb = new URBHandler(sender, hosts, myHost);
	}

	//Function to perform URB processes and call FIFO delivery
	public void handleReceivedMessage(Message message) {
		urb.bebDeliverMessage(message);
		fifoDeliver();
	}

	//Function to handle FIFO delivery
	public void fifoDeliver() {

		for (AppMessage m: urb.getDeliveredList()) {

			int messageSender = m.getOriginalSrcId();
			if (nextSequence.containsKey(messageSender) == false ) {
				nextSequence.put(messageSender, 1);
			}

			if (m.getContent() == nextSequence.get(messageSender)) {
				if (fifoDelivered.contains(m) == false) {
					System.out.println("Adding to fifo delivery: " + 
						m.getContentAsString() + " from " + Integer.toString(m.getOriginalSrcId()));
					fifoDelivered.add(m);
					nextSequence.put(messageSender, m.getContent() + 1);
				}
			}
		}
	}

	//Function to get the URB handler
	public static URBHandler getURBHandler() {
    	return urb;
    }

    //Function to get the FIFO delivery list of messages
	public static List<AppMessage> getDeliveredList() {
    	return fifoDelivered;
    }
}