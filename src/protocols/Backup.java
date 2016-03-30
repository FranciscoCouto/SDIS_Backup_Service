package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import communication.Control;
import communication.Send;
import utilities.Tools;

public class Backup extends Thread{
	
	private static String FILE;
	Path path;
	
	static String multicastIp, Version, PeerID;
	static int  MCBackup;
	static int repDeg;
		
	//private ArrayList<Chunk> list;
	private Control c2;
	
	public Backup(String File, int deg, String multicastIP, int mCBackup, String PeerId,Control c){
		
		FILE=File;
		multicastIp=multicastIP;
		MCBackup = mCBackup;
		Version="1.0";
		PeerID = PeerId;
		c2 = c;
		repDeg = deg;
	}
	
	@Override
	public void run() {
		
		int chunkNo = 0, count = 0;
		long time=1000;
		boolean found = false;
		
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
		
		while(count < 5 && chunkNo < times) {
				
				data = null;
				found = false;
				
				if((chunkNo+1) == times) {
					int lastsize = total.length - 64000*chunkNo;
					data = Tools.splitfile(path, chunkNo, lastsize);
				}
				
				data = Tools.splitfile(path, chunkNo, 64000);
				
				//String s1 = new String(data);

				byte[] msg = Tools.CreatePUTCHUNK(chunkNo,Version, PeerID, repDeg , data, fileID);
				
				
				
				try {
					s.send(msg);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //data in byte[]
				
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (int i = 0; i < c2.getStored().size(); i++) {
					if(c2.getStored().get(i).getFileId().equals(fileID) && c2.getStored().get(i).getChunkNo() == chunkNo){
						found = true;
					}
				}
				
				if(found && c2.getStored().size() >= ((chunkNo + 1) * repDeg)){
					chunkNo++;
					count = 0;
					time = 1000;
					System.out.println(" Backup success ");
				}
				
				else{
					System.out.println(" Error sending Chunk...trying again ");
					time *= 2;
					count++;
				}
		}
		
		if(chunkNo >= times) {
			System.out.println("Backup successful. Saving in map!");
			try {
				Tools.saveMap(fileID,Integer.valueOf(chunkNo));
			} catch (IOException e) {
				 //TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			System.out.println("Error backing up file after five times trying.");
		
		return;
	}
	
}
