package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Send{
	
	private static int PORT;
	private static String ADDR;
	private static int PORTCONTROL;
	private static String ADDRCONTROL;
	
	public Send(String multicastAddressStr,String serviceAddressStr, int multicastPort){
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
		ADDRCONTROL=serviceAddressStr;
	}
	
	
	public static void send(byte[] request) {
		
		try(MulticastSocket multicastSocket = new MulticastSocket(PORTCONTROL);){
			
		InetAddress group = InetAddress.getByName(ADDR);
		multicastSocket.joinGroup(group);

		byte[] buf = new byte[256];
		DatagramPacket multicastPacket = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(multicastPacket);

		String msg = new String(multicastPacket.getData());
		String[] parts = msg.split(":");
		ADDRCONTROL = parts[0];
		PORT = Integer.parseInt(parts[1].replaceAll("[^\\d.]", ""));

		System.out.println("multicast: " + ADDR + " "
				+ PORTCONTROL + ": " + ADDRCONTROL + " " + PORT);

		// build message
		

		// open socket
		DatagramSocket socket = new DatagramSocket();

		// send request
		//buf = request.getBytes();
		InetAddress address = InetAddress.getByName(ADDRCONTROL);
		DatagramPacket packet = new DatagramPacket(request, request.length, address,
				PORT);
		socket.send(packet);
		
		// receive response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());

		System.out.println(request.toString() + " :: " + response);

		// close socket
		socket.close();

		multicastSocket.leaveGroup(group);
		multicastSocket.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

}
