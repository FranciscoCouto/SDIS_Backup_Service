package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Send{

	private static String ADDR;
	private static int PORTCONTROL;
	
	public Send(String multicastAddressStr,int multicastPort){
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
	}
	
	
	public void send(byte[] request) throws IOException {

		// open socket
		DatagramSocket socket = new DatagramSocket();
		
		// send request
		InetAddress address = InetAddress.getByName(ADDR);
		DatagramPacket packet = new DatagramPacket(request, request.length, address, PORTCONTROL);
		//System.out.println(ADDR + "   " + PORTCONTROL + "  " + ADDRCONTROL);
		socket.send(packet);	
		
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// close socket
		socket.close();
		
		System.out.println("Message Sent!");
		
	}

}