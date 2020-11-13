package cs451;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

//Used as reference: https://beginnersbook.com/2014/01/how-to-append-to-a-file-in-java/

/* The FileHandler class is used to handle writing to the output file.
*/

public class FileHandler {

	/*  FileHandler consists of the following:
        file: file object
        writer: filewriter object
        bwriter: bufferedwriter object
    */

	private File file;
	private FileWriter writer;
	private BufferedWriter bwriter;

	public FileHandler(String outputFile) {
		
		try {
			this.file = new File(outputFile);
			if(!this.file.exists()){
				System.out.println("Creating output file: " + outputFile);
	    		this.file.createNewFile();
	    	}
			this.writer = new FileWriter(this.file);
			this.bwriter = new BufferedWriter(writer);
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

	//Function to write broadcast info
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

	//Function to write delivery info
	public void writeDeliverList(List<AppMessage> delivered) {

		String line;
		try {
			for (AppMessage message: delivered) {
				line = "d " + Integer.toString(message.getOriginalSrcId()) + " " + message.getContentAsString();
				bwriter.write(line);
				bwriter.newLine();
				bwriter.flush();
			}
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

	//Function to close bufferwriter
	public void close() {

		try {
			bwriter.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

}
