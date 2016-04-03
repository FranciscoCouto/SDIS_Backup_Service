package communication;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import peers.Chunk;
import peers.Peer;
import protocols.Backup;
import utilities.Tools;

public class Control extends Thread{

	private static int PORT, MCBackup,MCRestore;
	private static String ADDR;
	private String PeerID, multicastIPBackup,multicastIPRestore;

	
	private static volatile ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
	
	/**
	 * Class Constructor
	 * @param port
	 * @param end
	 * @param FILE
	 * @param peerID
	 */
	public Control(int port, String end,String peerID, int McBackup, String multicastipBackup, int McRestore, String multicastipRestore){
		PORT=port;
		ADDR=end;
		chunkList = new ArrayList<Chunk>();
		PeerID = peerID;
		MCBackup = McBackup;
		MCRestore = McRestore;
		multicastIPBackup = multicastipBackup;
		multicastIPRestore = multicastipRestore;
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(PORT);){
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
								
			}
			else if(Fields[0].toLowerCase().equals("getchunk") && !Tools.getPeerID(Fields[3]).equals(PeerID)) {
						
				File file = new File(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator +Fields[4]+"-"+Fields[3]+".bak");
				
				if(!file.exists()){ return;}
				
				Path path = Paths.get(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator +Fields[4]+"-"+Fields[3]+".bak");
				byte[] text = Files.readAllBytes(path);
				
				
				byte[] msg = Tools.CreateCHUNK(Integer.valueOf(Fields[4]),Fields[1], PeerID,text, Fields[3]);

				//System.out.println("CHUNKNO: "+header[4]);
				
				try {
					Thread.sleep(Tools.random(0,400));
					//Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Send s = new Send(multicastIPRestore,MCRestore);
				
				s.send(msg);
									
			}
			else if(Fields[0].toLowerCase().equals("delete") && !Fields[2].equals(PeerID)) {
				
				long freeSpace = Tools.removeFiles(Fields[3]);
				
				Tools.removeLineFromFile(Fields[3], "Rep", "Rep");
				Tools.removeLineFromFile(Fields[3], "Desired", "Rep");
				
				Tools.ChangeDiskSize("delete", freeSpace, PeerID);
				System.out.println("File Deleted");
			}
			else if(Fields[0].toLowerCase().equals("removed") && !Fields[2].equals(PeerID)) {
				
				//edit txt
				Tools.removeLine(Fields[3] + " " + Fields[4]);
				
				File file =new File(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator + Fields[3] + "-" + Fields[4]+".bak");
				
				int repReal = Tools.getRealRep(Fields[3], Fields[4]);
				int repDee = Tools.getChunkNoRep(Fields[3]);
				
				if(file.exists()) repReal += 1;
				
				if(repReal < repDee && file.exists()) {
					System.out.println("Initializing backup protocol from reclaim");
					Backup back = new Backup(Fields[4]+"-"+Fields[3], 1, multicastIPBackup, MCBackup, PeerID, this, "removed");
        			back.start();
				}				
			}
			else {
				System.out.println("Only watching control");
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
	
}
