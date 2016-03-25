package communication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import utilities.Tools;

public class Control extends Thread{
	

	private static int PORT;
	private static String ADDR;
	private static int PORTCONTROL;
	private static String ADDRCONTROL;

	public Control(int servicePort, String multicastAddressStr,String serviceAddressStr, int multicastPort){
		PORT=servicePort;
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
		ADDRCONTROL=serviceAddressStr;
	}

	
	@Override
	public void run() {
		
		//System.out.println("Listening ip: "+ADDR+" port: "+PORT);
		try(MulticastSocket multicastSocket = new MulticastSocket(8888);){
			
		InetAddress group = InetAddress.getByName("225.0.0.3");
		
		multicastSocket.joinGroup(group);
		multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */

		while (true) {
				
			//System.out.println("heyyyy");
			byte[] buf = new byte[64000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			// receive request
			multicastSocket.receive(packet);
			
			String msgRec = new String(packet.getData(), 0,
					packet.getLength());
		
			System.out.println("Recebi STORED: " + msgRec);

		}
		
		
		}
		
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		//System.out.println("heyyyy");
	}

}
