package protocols;

import java.io.IOException;

import communication.ReceiveRestore;
import communication.Send;
import utilities.Tools;

public class Restore extends Thread{
	
	private static String FILE;
	
	static String multicastIPControl,Version, PeerID;
	static int  MCControl;
	static int repDeg;
	private ReceiveRestore c1;
	
	/**
	 * Class constructor
	 * @param File
	 * @param multicastIP
	 * @param mCRestore
	 * @param PeerId
	 * @param c
	 */
	public Restore(String File, String multicastIPcontrol, int mCControl, String PeerId, ReceiveRestore c){
		
		FILE=File;
		multicastIPControl=multicastIPcontrol;
		MCControl = mCControl;
		Version="1.0";
		PeerID = PeerId;
		c1=c;
	}
	
	@Override
	public void run(){
		
		int count = 0,retry = 0;
		Send s = new Send(multicastIPControl, MCControl);
		String fileID = Tools.sha256(FILE+PeerID);
		int chunkNo = Tools.getChunkNo(fileID);
		
		//System.out.println("HIII: " + fileID);
		
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
			//System.out.println("TAMANHO " + c1.getStoredChunkNo().size());
			for(int i=0; i < c1.getStoredChunkNo().size(); i++) {
				if(c1.getStoredChunkNo().get(i).getFileId().equals(fileID) && 
						c1.getStoredChunkNo().get(i).getChunkNo() == count){
							count++;
							break;
				}
			}

		}
		
		return;
	}

}
