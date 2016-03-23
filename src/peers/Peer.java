package peers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Peer {
	
	static String multicastAddressStr;
	static int servicePort = 8080;
	static String serviceAddressStr;
	static int multicastPort;
	
	public Peer(int port, String IP){	
		multicastPort = port;
		multicastAddressStr = IP;
		serviceAddressStr = getIPv4();
	}
	
	public static void start() throws IOException{
		
		
		InetAddress group = InetAddress.getByName(multicastAddressStr);
		MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
		multicastSocket.joinGroup(group);

		byte[] buf = new byte[256];
		DatagramPacket multicastPacket = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(multicastPacket);

		String msg = new String(multicastPacket.getData());
		String[] parts = msg.split(":");
		serviceAddressStr = parts[0];
		servicePort = Integer.parseInt(parts[1].replaceAll("[^\\d.]", ""));

		System.out.println("multicast: " + multicastAddressStr + " "
				+ multicastPort + ": " + serviceAddressStr + " " + servicePort);

		// build message
		String request = "ola";

		// open socket
		DatagramSocket socket = new DatagramSocket();

		// send request
		buf = request.getBytes();
		InetAddress address = InetAddress.getByName(serviceAddressStr);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
				servicePort);
		socket.send(packet);

		// receive response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());

		System.out.println(request + " :: " + response);

		// close socket
		socket.close();

		multicastSocket.leaveGroup(group);
		multicastSocket.close();
		
	}

}
