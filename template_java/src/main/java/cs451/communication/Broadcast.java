package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.net.UnknownHostException;

public class Broadcast {

	private int status;
	private UDPServer myUDPServer;

	public Broadcast(Host myHost) {
		this.status = 0;

		try {
			this.myUDPServer = new UDPServer(myHost.getPort(), InetAddress.getByName(myHost.getIp()));
			this.myUDPServer.start();
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public boolean sendMessages(int totalMessages, List<Host> hosts, Host myHost) {
        //For each message
        //For each host
        //Send to all hosts
        try {
	        for (int m = 1; m < totalMessages + 1; m++) {
	            System.out.println("Sending message " + Integer.toString(m));
	            for (Host host: hosts) {
	            	System.out.println("Message to: " + Integer.toString(host.getPort()));
	            	this.myUDPServer.sendMessage(m, InetAddress.getByName(host.getIp()), host.getPort());
	            }
	        }
	    } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}