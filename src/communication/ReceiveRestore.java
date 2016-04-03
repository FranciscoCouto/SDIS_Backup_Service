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
import utilities.Tools;

public class ReceiveRestore extends Thread{

	private static String CADDR, PeerID;
	private static int CPORT;
	private static volatile ArrayList<Chunk> chunkNoList = new ArrayList<Chunk>();
	boolean exists;
	
	/**
	 * Class Constructor
	 * @param address
	 * @param port
	 * @param ControlAdd
	 * @param ControlP
	 */
	public ReceiveRestore( String ControlAdd, int ControlP, String Peerid){
		CPORT = ControlP;
		CADDR = ControlAdd;
		PeerID = Peerid;
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(CPORT);){
			
		InetAddress group = InetAddress.getByName(CADDR);
		
		multicastSocket.joinGroup(group);
		//multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */

		while (true) {
			
			exists = false;
			
			byte[] buf = new byte[67000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			// receive request
			multicastSocket.receive(packet);
			
			String[] header = Tools.convertHeader(packet.getData());

			
			if(header[0].toLowerCase().equals("chunk") && !header[2].equals(PeerID)){
				
				int garbage = Tools.convertBody(packet.getData());
				byte[] data = Tools.trim(packet.getData(),garbage);
				
				Chunk c = new Chunk(header[3], Integer.valueOf(header[4].trim()), header[2]);
				
				for(int i=0; i < chunkNoList.size(); i++) {
					if(chunkNoList.get(i).getFileId().equals(header[3]) && 
							chunkNoList.get(i).getChunkNo() == Integer.valueOf(header[4].trim())){
								exists = true;
								System.out.println("Chunk already exists!");
					}
				}

				if(!exists){ 
					chunkNoList.add(c);
					Tools.RestoreFile(header[4], header[3], data, Peer.fileName);
					System.out.println("Chunk stored!");
				}
				
			}
			
			else{
				System.out.println("INVALID TYPE OF MESSAGE RECEIVED!");
				return;
			}
		}
		
		
		}
		
		catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public ArrayList<Chunk> getStoredChunkNo(){	
		return chunkNoList;		
	}
}
