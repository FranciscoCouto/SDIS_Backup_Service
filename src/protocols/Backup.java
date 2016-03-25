package protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
		Send s = new Send(multicastIp, myip, MCBackup);
		
		byte[] total = null;
		try {
			total = Files.readAllBytes(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String fileID = Tools.sha256(FILE+PeerID);
		int times = (int) Math.ceil((double)total.length / 64000);
		
		while(count < 5 && chunkNo < times) {
			
				byte[] data = splitfile(FILE, chunkNo);
			
				Chunk c = new Chunk(fileID, chunkNo, data); //FAZER SHA256 para o ID
				
				String msg = CreatePUTCHUNK(c.getChunkNo(), 1 , data);
				
				s.send(msg.getBytes()); //data in byte[]
				
				chunkNo++;
				
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
	
	public static byte[] splitfile(String path, int chunkNo){
		
			File file = new File(path);
			byte fileContent[] = new byte[64000];
	        FileInputStream fin = null;
	        
	        try {
	            // create FileInputStream object

	            fin = new FileInputStream(file);

	            // Reads up to certain bytes of data from this input stream into an array of bytes.
	            fin.read(fileContent, chunkNo*64000, 64000);
	            
	            try {
	                if (fin != null) {
	                    fin.close();
	                }
	            }
	            catch (IOException ioe) {
	                System.out.println("Error while closing stream: " + ioe);
	            }
	            //create string from byte array
	            return fileContent;
	        }
	        catch (FileNotFoundException e) {
	            System.out.println("File not found" + e);
	        }
	        catch (IOException ioe) {
	            System.out.println("Exception while reading file " + ioe);
	        }
	        
            return fileContent;

	}
}
