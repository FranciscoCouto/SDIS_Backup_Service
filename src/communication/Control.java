package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import peers.Chunk;
import peers.Peer;
import protocols.Backup;
import utilities.Tools;

public class Control extends Thread{
	

	private static int PORT, MCBackup;
	private static String ADDR;
	private String PeerID, multicastIPBackup;


	
	private static volatile ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
	private static volatile ArrayList<Integer> chunkNoList = new ArrayList<Integer>();
	
	/**
	 * Class Constructor
	 * @param port
	 * @param end
	 * @param FILE
	 * @param peerID
	 */
	public Control(int port, String end,String peerID, int McBackup, String multicastipBackup){
		PORT=port;
		ADDR=end;
		chunkList = new ArrayList<Chunk>();
		PeerID = peerID;
		MCBackup = McBackup;
		multicastIPBackup = multicastipBackup;
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(PORT);){
		int hello = 0;
		InetAddress group = InetAddress.getByName(ADDR);
		
		multicastSocket.joinGroup(group);
		//multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */
		
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
			
			if(Fields[0].toLowerCase().equals("stored") && !Fields[2].equals(PeerID)) {
				Chunk c = new Chunk(Fields[3], Integer.valueOf(Fields[4].trim()), Fields[2]);
				
				for(int i=0; i < chunkList.size(); i++) {
					if(chunkList.get(i).getFileId().equals(Fields[3]) && 
							chunkList.get(i).getChunkNo() == Integer.valueOf(Fields[4].trim())
							 && chunkList.get(i).getPeerID().equals(Fields[2])){
								exists = true;
					}
				}

				if(!exists){ 
					chunkList.add(c);
					Tools.saveFIDCKNO(Fields[4].trim(), Fields[3]);
				}
				
				hello++;
				System.out.println("HEEYY: " + hello);
				
			}
			else if(Fields[0].toLowerCase().equals("chunk") && Tools.getPeerID(Fields[3]).equals(PeerID)) {
				
				int garbage = Tools.convertBody(packet.getData());
				byte[] data = Tools.trim(packet.getData(),garbage);
			
				if(!chunkNoList.contains(Integer.valueOf(Fields[4]))) {
					chunkNoList.add(Integer.valueOf(Fields[4]));
					Tools.RestoreFile(Fields[4], Fields[3], data, Peer.fileName);
					
					System.out.println("Recieved chunk com chunkNO: " +  Fields[4]);
				}
					
				System.out.println("That chunk has already been stored!");
			}
			else if(Fields[0].toLowerCase().equals("delete") && !Fields[2].equals(PeerID)) {
				
				long freeSpace = Tools.removeFiles(Fields[3]);

				Tools.ChangeDiskSize("delete", freeSpace, PeerID);
				System.out.println("File Deleted");
			}
			else if(Fields[0].toLowerCase().equals("removed") && !Fields[2].equals(PeerID)) {
				
				//edit txt
				Tools.removeLine(Fields[3] + " " + Fields[4]);
				
				int repReal = Tools.getRealRep(Fields[3], Fields[4]);
				int repDee = Tools.getChunkNoRep(Fields[3]);
				
				if(repReal < repDee) {
					Backup back = new Backup(Fields[4]+"-"+Fields[3]+".bak", 1, multicastIPBackup, MCBackup, PeerID, this, "removed");
        			back.start();
				} else {
					System.out.println("File Removed");
				}
				
				
				System.out.println("File Removed");
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
