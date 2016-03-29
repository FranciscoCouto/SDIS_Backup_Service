package protocols;

import java.io.IOException;

import communication.Send;
import utilities.Tools;

public class Restore extends Thread{
	
	private static String FILE;
	
	static String multicastIp, myip, Version, PeerID;
	static int  MCRestore;
	static int repDeg;
	
	public Restore(String File, String multicastIP, String iPv4A, int mCRestore, String PeerId){
		
		FILE=File;
		multicastIp=multicastIP;
		myip = iPv4A;
		MCRestore = mCRestore;
		Version="1.0";
		PeerID = PeerId;
	}
	
	@Override
	public void run(){
		
		int count = 0,retry = 0;
		Send s = new Send(multicastIp, MCRestore);
		String fileID = Tools.sha256(FILE+PeerID);
		int chunkNo = Tools.getChunkNo(fileID);
		
		System.out.println("HIII: " + fileID);
		
		if(chunkNo == 0){
			System.out.println("This File was never backed up!!");
			return;
		}
		
		while(chunkNo > count && retry < 5){

			String msg = Tools.CreateGETCHUNK(count,Version, PeerID, fileID); //passamos count para começarmos do inicio (0)
			
			count++;
			try {
				s.send(msg.getBytes());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //data in byte[]
			
		}
		
		return;
	}

}
