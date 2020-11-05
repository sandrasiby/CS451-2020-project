package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;

public class Receiver extends Thread {

	//private UDPLink uLink;
	private boolean running;
	private Sender sender;

	public Receiver(Sender sender, int port, InetAddress address) throws IOException {
		//this.uLink = new UDPLink(port, address);
		this.sender = sender;
	}

	public void run() {

        running = true;
        Message receivedMsg;
        System.out.println("Start Receiver Thread");
 
        while (running) {
 			receivedMsg = sender.getLink().receiveMessage();
 			String msgType = receivedMsg.getType();

 			if (msgType == "NORMAL") {
 				//Got message: send ACK
 				Message ackMessage = new Message(receivedMsg.getContent(),
 					receivedMsg.getDstAddress(), receivedMsg.getDstPort(),
 					receivedMsg.getSrcAddress(), receivedMsg.getSrcPort(), "ACK");
 				sender.sendMessage(ackMessage);
 			} else if (msgType == "ACK") {
 				//Got ack: update sentStatus
 				Integer oldStatus = sender.sentStatus.replace(receivedMsg, 1);
 			}
        }
    }
}

