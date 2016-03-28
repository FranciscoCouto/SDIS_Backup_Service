package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import peers.Chunk;

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

			System.out.println("CONTROL1: " + chunkList.size());
			
			Chunk c = new Chunk(Fields[3], Integer.valueOf(Fields[4].trim()), Fields[2]);
			
			chunkList.add(c);

			
			System.out.println("CONTROL2: " + chunkList.size());
			
			System.out.println("Recebi STORED: " + msgRec);

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
