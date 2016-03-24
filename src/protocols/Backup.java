package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import communication.Send;

public class Backup extends Thread{
	
	private static String FILE;
	Path path;
	
	static String multicastIp;
	static String myip;
	static int  MCBackup;
	
	public Backup(String File, String multicastIP, String iPv4A, int mCBackup){
		
		FILE=File;
		multicastIp=multicastIP;
		myip = iPv4A;
		MCBackup = mCBackup;
	}
	
	@Override
	public void run() {
		
		path = Paths.get(FILE);
		try {
			byte[] data = Files.readAllBytes(path);
			Send s = new Send(multicastIp, myip, MCBackup);
			s.send(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void CreateBMessage(){
		
	
		
	}
}
