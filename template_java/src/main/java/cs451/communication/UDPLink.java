package cs451;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*; 
import java.lang.ClassNotFoundException;

// I used these links as reference:
// https://www.baeldung.com/udp-in-java
// https://www.pegaxchange.com/2018/01/23/simple-udp-server-and-client-socket-java/

public class UDPLink {

    private DatagramSocket socket;
    private boolean running;
    private int port;
    private InetAddress address;
    
    public UDPLink(int port, InetAddress address) throws IOException {
        this.port = port;
        this.address = address;
        socket = new DatagramSocket(this.port, this.address);
    }

    public int sendMessage(Message message) {

        byte[] buf;

        try {
        	//System.out.println("In UDPsend, Try to send: " + message.getContent());
            buf = serializeMessage(message);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, message.getDstAddress(), message.getDstPort());
            socket.send(packet);       
        } catch (SocketException e) {
            System.err.println("Problem with the sending UDP packet!");
            return -1;
        }  catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }


    public Message receiveMessage() {

    	Message receivedMsg;
    	int receivedBytes = 0;
    	byte[] buf = new byte[2048];
    	
    	try {
    		DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            receivedBytes = packet.getLength(); 
            byte[] myObject = new byte[receivedBytes];
			for(int i = 0; i < receivedBytes; i++)
			{
			     myObject[i] = buf[i];
			}
            receivedMsg = deserializeMessage(myObject);
            return receivedMsg;
	    } catch (SocketException e) {
        	System.err.println("Problem with the receiving UDP packet!");
        	//return -1;
        } catch (IOException e) {
        	System.err.println("io exception in receiveMessage");
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
    	socket.close();
    }

    // For serialization/deserialization, I used: 
    // https://stackoverflow.com/questions/4252294/sending-objects-across-network-using-udp-in-java
    	
    public byte[] serializeMessage(Message message) {

    	try {
	    	ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(bStream); 
			oo.writeObject(message);
			oo.close();

			byte[] serializedMessage = bStream.toByteArray();
			return serializedMessage;
		}  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message deserializeMessage(byte[] data) {
    	
    	try {
    		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
	    	//System.out.println("got stream");
			Message message = (Message) iStream.readObject();
			//System.out.println("in deserialization " + message.getContent() + Integer.toString(message.getSrcPort()));
			iStream.close();
			return message;
		}  catch (IOException e) {
			System.out.println("in exception");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        }
        return null;
    }
}
