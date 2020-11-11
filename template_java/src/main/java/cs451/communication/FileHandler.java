package cs451;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

//Used as reference: https://beginnersbook.com/2014/01/how-to-append-to-a-file-in-java/

public class FileHandler {

	private File file;
	private FileWriter writer;
	private BufferedWriter bwriter;

	public FileHandler(String outputFile) {
		
		try {
			this.file = new File(outputFile);
			if(!this.file.exists()){
	    	   this.file.createNewFile();
	    	}
			this.writer = new FileWriter(this.file, true);
			this.bwriter = new BufferedWriter(writer);
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void writeBroadcastList(List<Message> broadcasted) {
		
		String line;
		try {
			for (Message message: broadcasted) {
				line = "b " + message.getContentAsString();
				bwriter.write(line);
				bwriter.newLine();
				bwriter.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeDeliverMessage(Message message) {

		try {
			String line = "d " + Integer.toString(message.getSrcId()) + " " + message.getContent();
			System.out.println("writing: " + line);
			bwriter.write(line);
			bwriter.newLine();
			bwriter.flush();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void writeDeliverList(List<Message> delivered) {

		String line;
		try {
			for (Message message: delivered) {
				line = "d " + Integer.toString(message.getSrcId()) + " " + message.getContent();
				bwriter.write(line);
				bwriter.newLine();
				bwriter.flush();
			}
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void close() {

		try {
			bwriter.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

}
