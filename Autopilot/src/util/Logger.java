package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	
	private FileWriter fw;
	private BufferedWriter bw;
	
	
	public Logger(String title) {
		try {
			fw = new FileWriter(title);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(String write) {
		try {
			bw.append(write + "\n");
			bw.newLine();
			bw.close();					
			fw.close();
			System.out.println("File Closed");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
