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
	
	private Control packets;
	
	private ArrayList<Chunk> list;
	
	public Backup(String File, String multicastIP, String iPv4A, int mCBackup, String PeerId){
		
		FILE=File;
		multicastIp=multicastIP;
		myip = iPv4A;
		MCBackup = mCBackup;
		Version="1.0";
		PeerID = PeerId;
		list = packets.getStored();
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
		
		if((double)total.length % 64000 == 0) {
			times=+1;
		}
		
		while(count < 5 && chunkNo < times) {
				
				data = null;
				
				if((chunkNo+1) == times) {
					int lastsize = total.length - 64000*chunkNo;
					data = Tools.splitfile(path, chunkNo, lastsize);
				}
				data = Tools.splitfile(path, chunkNo, 64000);
				
				String s1 = new String(data);
				//FAZER SHA256 para o ID
				//System.out.println("NO:  " + chunkNo + "   :::  " + c.getChunkNo());
				//ALTERAR PARA PASSAR DATA COMO BYTE
				//sakfhsajkdas
				//asjdhaskd
				String msg = Tools.CreatePUTCHUNK(chunkNo,Version, PeerID, 1 , s1, fileID);

				//chunkList.size();
				System.out.println("OLAAA: " + list.size());
				
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
