package cs451;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.io.Serializable;

public class AppMessage {

	private int msgContent;
	private int originalSrcId;

	public AppMessage(int msgContent, int originalSrcId) {

		this.msgContent = msgContent;
		this.originalSrcId = originalSrcId;
	}

	public int getContent() {
		return msgContent;
	}

	public int getOriginalSrcId() {
		return originalSrcId;
	}

	public String getKey() {
		return Integer.toString(msgContent) + "_" + Integer.toString(originalSrcId);
	}

	@Override
	public boolean equals(Object obj) {

		try {
			if(obj == this) return true; 
			if((obj == null) || (obj.getClass() != this.getClass())) return false; 
			AppMessage m = (AppMessage) obj;
			if ((this.getContent() == m.getContent()) && 
					(this.getOriginalSrcId() == m.getOriginalSrcId())) 
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 1;
	}
}