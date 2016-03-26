package communication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	private static int PORTCONTROL;
	private static String ADDRCONTROL;

	public ArrayList<Chunk> chunkList;
	
	public Control(int servicePort, String multicastAddressStr,String serviceAddressStr, int multicastPort, ArrayList<Chunk> chunkList){
		PORT=servicePort;
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
		ADDRCONTROL=serviceAddressStr;
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(8888);){
			
		InetAddress group = InetAddress.getByName("225.0.0.3");
		
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
			
			for (int i = 0; i < chunkList.size(); i++) {
				if(chunkList.get(i).getFileId() == Fields[3] && chunkList.get(i).getChunkNo() == Integer.valueOf(Fields[4])){	
					chunkList.remove(i);
				}
			}
			
			System.out.println("CONTROL2: " + chunkList.size());
			
			System.out.println("Recebi STORED: " + msgRec);

		}
		
		
		}
		
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
