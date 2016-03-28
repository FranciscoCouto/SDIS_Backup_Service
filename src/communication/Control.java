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
	
	private static volatile ArrayList<Chunk> chunkList = new ArrayList<Chunk>();;
	
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

		while (true) {
			
			byte[] buf = new byte[64000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			// receive request
			multicastSocket.receive(packet);
			
			String msgRec = new String(packet.getData(), 0,
					packet.getLength());
			
			String[] Fields = msgRec.split(" ");
			
			if(Fields[0].equals("stored")) {
				Chunk c = new Chunk(Fields[3], Integer.valueOf(Fields[4].trim()));
			
				chunkList.add(c);

				System.out.println("Recebi STORED: " + msgRec);
			}
			else if(Fields[0].equals("chunk")) {
				
				String data =  Tools.convertBody(packet.getData()).trim();
				
				Tools.SaveChunks(Fields[4], Fields[3], data);
				
				System.out.println("Recebi chunk");
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

}
