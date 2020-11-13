package cs451;

import java.io.IOException;

/* The ShutdownHook class is used to handle process crash.
*/

public class ShutdownHook extends Thread {

	//Function to write to files on shut down
	@Override
	public void run(){
		System.out.println("Shutting down and writing to files");
		Receiver.getFileHandler().writeBroadcastList(Receiver.getSender().getBroadcastList());
		//Receiver.getFileHandler().writeDeliverList(Receiver.getDeliveredList());
		//Receiver.getFileHandler().writeDeliverList(Receiver.getURBHandler().getDeliveredList());
		Receiver.getFileHandler().writeDeliverList(Receiver.getFIFOHandler().getDeliveredList());
		Receiver.getFileHandler().close();
	}
}