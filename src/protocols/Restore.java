package protocols;

import java.io.IOException;

import communication.Control;
import communication.Send;
import utilities.Tools;

public class Restore extends Thread{
	
	private static String FILE;
	
	static String multicastIp,Version, PeerID;
	static int  MCRestore;
	static int repDeg;
	private Control c1;
	
	public Restore(String File, String multicastIP, int mCRestore, String PeerId, Control c){
		
		FILE=File;
		multicastIp=multicastIP;
		MCRestore = mCRestore;
		Version="1.0";
		PeerID = PeerId;
		c1=c;
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
					
			try {
				s.send(msg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //data in byte[]
						
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("TAMANHO " + c1.getStoredChunkNo().size());
			if(c1.getStoredChunkNo().contains(count)) {
				count++;
			}
			else {
				System.out.println("ERRRO");
			}
		}
		
		return;
	}

}
