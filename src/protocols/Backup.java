package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import communication.Send;
import peers.Chunk;

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
		
		
		path = Paths.get(FILE);
	
			try {
				byte[] data = Files.readAllBytes(path);
				Chunk c = new Chunk("1", 1, data);
				String msg = CreatePUTCHUNK(c.getChunkNo(), 1 , data);
				Send s = new Send(multicastIp, myip, MCBackup);
				s.send(msg.getBytes()); //data in byte[]
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	/**
	 * PUTCHUNK
	 * <version>
	 * <SenderID>
	 * <FieldID>
	 * <ChunkNo>
	 * <ReplicationDeg>
	 * <CRLF>
	 * <CRLF>
	 * <Body>
	 */
	public static String CreatePUTCHUNK(int ChunkNo, int replicationDeg, byte[] data){
		
		String BuildMessage = "PUTCHUNK" + " " + Version + " " + PeerID + " " + //File ID
				+ ChunkNo + " " + replicationDeg + " " + "\r" + "\n" + "\r" + "\n" + data;  
				
		return BuildMessage;		
	}
}
