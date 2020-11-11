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

public class ShutdownHook extends Thread {

	@Override
	public void run(){
		System.out.println("Shutting down and writing to files");
		Receiver.getFileHandler().writeBroadcastList(Receiver.getSender().getBroadcastList());
		Receiver.getFileHandler().writeDeliverList(Receiver.getDeliveredList());
		Receiver.getFileHandler().close();
	}
}