package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import peers.Chunk;
import utilities.Tools;

public class Control extends Thread{
	

	private static int PORT;
	private static String ADDR;
	
	private static volatile ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
	private static volatile ArrayList<Integer> chunkNoList = new ArrayList<Integer>();
	
	public Control(int port, String end){
		PORT=port;
		ADDR=end;
		chunkList = new ArrayList<Chunk>();
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(PORT);){
			
		InetAddress group = InetAddress.getByName(ADDR);
		
		multicastSocket.joinGroup(group);
		multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */
		
		boolean exists = false;
		
		while (true) {
			
			exists = false;
			byte[] buf = new byte[67000];
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			// receive request
			multicastSocket.receive(packet);
			
			String msgRec = new String(packet.getData(), 0,
					packet.getLength());
		
			String[] Fields = msgRec.split(" ");
			
			if(Fields[0].toLowerCase().equals("stored")) {
				Chunk c = new Chunk(Fields[3], Integer.valueOf(Fields[4].trim()), Fields[2]);
				
				for(int i=0; i < chunkList.size(); i++) {
					if(chunkList.get(i).getFileId().equals(Fields[3]) && 
							chunkList.get(i).getChunkNo() == Integer.valueOf(Fields[4].trim())
							 && chunkList.get(i).getPeerID().equals(Fields[2])){
								exists = true;
					}
				}
				
				if(!exists) 
					chunkList.add(c);
				
			}
			else if(Fields[0].toLowerCase().equals("chunk")) {
				
				int garbage = Tools.convertBody(packet.getData());
				byte[] data = Tools.trim(packet.getData(),garbage);
			
				chunkNoList.add(Integer.valueOf(Fields[4]));
				
				Tools.RestoreFile(Fields[4], Fields[3], data);
				
				System.out.println("Recebi chunk com chunkNO: " +  Fields[4]);
			}
			else if(Fields[0].toLowerCase().equals("delete")) {
				
				Tools.removeFiles(Fields[3]);
				
				System.out.println("File Deleted");
			}
			else {
				System.out.println("Wrong message control");
			}
		}
		
		
		}
		
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public ArrayList<Chunk> getStored(){	
		return chunkList;		
	}
	
	public ArrayList<Integer> getStoredChunkNo(){	
		return chunkNoList;		
	}
}
