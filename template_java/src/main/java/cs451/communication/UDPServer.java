package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

// I used these links as reference:
// https://www.baeldung.com/udp-in-java
// https://www.pegaxchange.com/2018/01/23/simple-udp-server-and-client-socket-java/

public class UDPServer extends Thread {
	 
    private byte[] buf = new byte[256];

    private DatagramSocket socket;
    private boolean running;
    private int port;
    private InetAddress address;

    //Replace with address and port
    public UDPServer(int port, InetAddress address) throws IOException {
        this.port = port;
        this.address = address;
        socket = new DatagramSocket(this.port, this.address);
    }

    public void run() {
        
        running = true;
        System.out.println("Start run");
 
        while (running) {
            try {
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            String message = new String(packet.getData()).trim();
	            System.out.println("Message from " + packet.getAddress().getHostAddress() +
	            	":" + Integer.toString(packet.getPort())  
	             + " : " + message);
	        } catch (SocketException e) {
            	System.err.println("Problem with the receiving UDP packet!");
            	//return -1;
        	} catch (IOException e) {
            	e.printStackTrace();
        	}
        }
        
        socket.close();
    }

    public int sendMessage(int message, InetAddress dstAddress, int dstPort) {

        byte[] buf;

        try {
            buf = Integer.toString(message).getBytes(); //Format of message -- str or int?
            DatagramPacket packet = new DatagramPacket(buf, buf.length, dstAddress, dstPort);
            socket.send(packet);       
        } catch (SocketException e) {
            System.err.println("Problem with the sending UDP packet!");
            return -1;
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
