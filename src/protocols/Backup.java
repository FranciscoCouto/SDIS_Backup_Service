package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import communication.Control;
import communication.Send;
import peers.Chunk;
import utilities.Tools;

public class Backup extends Thread{
	
	private static String FILE;
	Path path;
	
	static String multicastIp, myip, Version, PeerID;
	static int  MCBackup;
	static int repDeg;
		
	//private ArrayList<Chunk> list;
	private Control c2;
	
	public Backup(String File, int deg, String multicastIP, String iPv4A, int mCBackup, String PeerId,Control c){
		
		FILE=File;
		multicastIp=multicastIP;
		myip = iPv4A;
		MCBackup = mCBackup;
		Version="1.0";
		PeerID = PeerId;
		c2 = c;
		repDeg = deg;
	}
	
	@Override
	public void run() {
		
		int chunkNo = 0, count = 0;;
		
		path = Paths.get(FILE);
		Send s = new Send(multicastIp, MCBackup);
		
		byte[] total = null, data = null;
		try {
			total = Files.readAllBytes(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String fileID = Tools.sha256(FILE+PeerID);
		int times = (int) Math.ceil((double)total.length / 64000);
		
		System.out.println("times:   " + times);
		
		if((double)total.length % 64000 == 0) {
			times=+1;
		}
		
		while( chunkNo < times) {
				
				data = null;
				
				/*if((chunkNo+1) == times) {
					int lastsize = total.length - 64000*chunkNo;
					data = Tools.splitfile(path, chunkNo, lastsize);
				}*/
				
				data = Tools.splitfile(path, chunkNo, 64000);
				
				String s1 = new String(data);

				String msg = Tools.CreatePUTCHUNK(chunkNo,Version, PeerID, repDeg , s1, fileID);
				
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
				
				for (int i = 0; i < c2.getStored().size(); i++) {
					if(c2.getStored().get(i).getFileId().equals(fileID) && c2.getStored().get(i).getChunkNo() == chunkNo){
						chunkNo++;
						//try {
						//	Tools.saveMap(fileID,Integer.valueOf(chunkNo));
						//} catch (IOException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
						//}
						count = 0;
						
					}
					else {
						count++;
						
					}
				}
		}
	}
	
}
