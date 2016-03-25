package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import communication.Send;
import peers.Chunk;
import utilities.Tools;

public class Backup extends Thread{
	
	private static String FILE;
	Path path;
	
	static String multicastIp, myip, Version, PeerID;
	static int  MCBackup;
	
	public Backup(String File, String multicastIP, String iPv4A, int mCBackup, String PeerId){
		
		FILE=File;
		multicastIp=multicastIP;
		myip = iPv4A;
		MCBackup = mCBackup;
		Version="1.0";
		PeerID = PeerId;
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
		
		while(count < 5 && chunkNo < times) {
				
				data = null;
				data = Tools.splitfile(path, chunkNo);
			
				Chunk c = new Chunk(fileID, chunkNo, data); //FAZER SHA256 para o ID
				
				String msg = Tools.CreatePUTCHUNK(c.getChunkNo(),Version, PeerID, 1 , data);
				
				try {
					s.send(msg.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //data in byte[]
				
				chunkNo++;
				
		}
	}
	
}
