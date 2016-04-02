package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Send{

	private static String ADDR;
	private static int PORTCONTROL;
	
	/**
	 * Class Constructor
	 * @param multicastAddressStr
	 * @param multicastPort
	 */
	public Send(String multicastAddressStr,int multicastPort){
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
	}
	
	/**
	 * Send the packet
	 * @param request
	 * @throws IOException
	 */
	public void send(byte[] request) throws IOException {

		// open socket
		DatagramSocket socket = new DatagramSocket();
		
		// send request
		InetAddress address = InetAddress.getByName(ADDR);
		DatagramPacket packet = new DatagramPacket(request, request.length, address, PORTCONTROL);
		
		socket.send(packet);
				
		// close socket
		socket.close();
		
		System.out.println("Message Sent!");
		
	}

}