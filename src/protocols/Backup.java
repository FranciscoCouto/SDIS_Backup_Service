package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import communication.Send;

public class Backup extends Thread{
	
	private static String FILE;
	Path path;
	
	public Backup(String File){
		
		FILE=File;		
	}
	
	@Override
	public void run() {
		
		path = Paths.get(FILE);
		try {
			byte[] data = Files.readAllBytes(path);
			Send.send(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void CreateBMessage(){
		
	
		
	}
}
