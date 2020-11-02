package cs451;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class ConfigParser {

    private String path;

    public boolean populate(String value) {
        File file = new File(value);
        path = file.getPath();
        return true;
    }

    public String getPath() {
        return path;
    }

    public int getTotalMessages() {

    	String filename = getPath();
    	int messages = 0;

    	try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            messages = Integer.parseInt(line.trim());
        } catch (IOException e) {
            System.err.println("Problem with the config file!");
            return -1;
        }
    	return messages;
    }

}
