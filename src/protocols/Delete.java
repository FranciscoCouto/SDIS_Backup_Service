package protocols;

import java.io.File;
import java.io.IOException;

import communication.Send;
import utilities.Tools;

public class Delete extends Thread {
	
	static String CONTROLIP, Version,PeerID;
	static int  ControlPORT;
	private static String FILE;
	
	public Delete(String File, String ControlIP, int ControlPort, String PeerId){
		
		CONTROLIP = ControlIP;
		ControlPORT = ControlPort;
		FILE = File;
		Version="1.0";
		PeerID = PeerId;
		
	}
	
	@Override
	public void run(){
		
		String fileID = Tools.sha256(FILE+PeerID);
		
		int chunkNo = Tools.getChunkNo(fileID);
		
		if(chunkNo == 0){
			System.out.println("This File was never backed up!!");
			return;
		}
		
		int counter=0;
		
		while(counter < 1){
			
			String msg = Tools.CreateDelete(Version, PeerID, fileID);
			
			
			Send s = new Send("225.0.0.3",ControlPORT);
			
			try {
				s.send(msg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter++;
			try {
				Tools.removeLineFromFile(System.getProperty("user.dir") + File.separator + "Map" + File.separator+ "Map.txt",fileID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	}

}