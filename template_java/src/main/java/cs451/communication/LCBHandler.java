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

public class LCBHandler {

	/*  FIFOHandler consists of the following:
		urb: URB Handler that handles URB delivery/broadcast and gets messages for delivery
		fifoDelivered: List of messages in FIFO ordered. Used as input for the FileHandler
		to write into output file.
		nextSequence: Hashmap to keep track of sequence for a process' messages

		Note: Message content acts as the sequence for messages. 
    */

	public ConcurrentHashMap<Integer, Integer> nextSequence;
	public static List<AppMessage> lcbDelivered;
	public static URBHandler urb;
	private static Sender sender;
	private Host myHost;
	private int lastOwnDelivered; 

	public LCBHandler(Sender sender, List<Host> hosts, Host myHost) {

		this.nextSequence = new ConcurrentHashMap<Integer, Integer>(); //initialize to 1
		this.lcbDelivered = new ArrayList<>();
		this.sender = sender;
		this.urb = new URBHandler(sender, hosts, myHost);
		this.lastOwnDelivered = 0;
		this.myHost = myHost;
	}

	//Function to perform URB processes and call FIFO delivery
	public void handleReceivedMessage(Message message) {
		urb.bebDeliverMessage(message);
		lcbDeliver();
	}

	public void lcbDeliver() {

		int holdLCBDeliver = 0;
		List<AppMessage> toRemove = new ArrayList<>();
		List<AppMessage> pendingList = urb.getPendingList();

		for (AppMessage m: pendingList) {
			int messageSender = m.getOriginalSrcId();

			if (messageSender != myHost.getId()) {
				int[] messageVectorClock = m.getVectorClock();
				int[] processVectorClock = sender.getProcessVectorClock();

				for (int i = 0; i < messageVectorClock.length; i++) {
					if (messageVectorClock[i] > processVectorClock[i]) {
						//System.out.println("Holding message:");
						//m.printMessage();
						holdLCBDeliver += 1;
					}
				}

				if (holdLCBDeliver == 0) {
					System.out.println("Delivering message:");
					m.printMessage();
					System.out.println("Process clock:");
					printVectorClock(processVectorClock);
					lcbDelivered.add(m);
					LogMessage lm = new LogMessage(m.getContent(), m.getOriginalSrcId(), "d");
					//lm.printMessage();
					sender.updateAllList(lm);
					toRemove.add(m);
					sender.updateVectorClock(messageSender-1);
					System.out.println("Updated process clock to:");
					printVectorClock(sender.getProcessVectorClock());
				}
			} else {
				if (m.getContent() == lastOwnDelivered + 1) {
					lcbDelivered.add(m);
					LogMessage lm = new LogMessage(m.getContent(), m.getOriginalSrcId(), "d");
					//lm.printMessage();
					sender.updateAllList(lm);
					toRemove.add(m);
					lastOwnDelivered += 1;
				}
			}
		}

		for (AppMessage m: toRemove) {
			urb.removeFromPendingList(m);
		}
	}

	public void printVectorClock(int[] vectorClock) {
		String vc = " | ";
		for (int i = 0; i < vectorClock.length; i++) {
			vc += Integer.toString(vectorClock[i]) + " | ";
		}
		System.out.println(vc);
	}
	//Function to get the URB handler
	public static URBHandler getURBHandler() {
    	return urb;
    }

    //Function to get the LCB delivery list of messages
	public static List<AppMessage> getDeliveredList() {
    	return lcbDelivered;
    }
}