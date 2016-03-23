package peers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Peer {
	
	static String multicastAddressStr = "225.0.0";
	static int servicePort = 8080;
	static String serviceAddressStr = getIPv4();
	static int multicastPort = 8000;
	
	public Peer(int port, String IP, String LocalIP){	
		super();
	}
	
	public static void Listen() throws IOException{
		
		MulticastSocket multicastSocket = new MulticastSocket();
		multicastSocket.setTimeToLive(1);

		InetAddress multicastAddress = InetAddress
				.getByName(multicastAddressStr);

		// open server socket
		DatagramSocket serverSocket = new DatagramSocket(servicePort);
		serverSocket.setSoTimeout(1000);

		// 1s interval advertisement control variables
		long elapsedTime = 1000;
		long prevTime = System.currentTimeMillis();

		boolean done = false;
		while (!done) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				// receive request
				serverSocket.receive(packet);
				String request = new String(packet.getData(), 0,
						packet.getLength());

				// process request
				String response = "11";
				
				// send response
				buf = response.getBytes();
				InetAddress cliAddress = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, cliAddress, port);
				serverSocket.send(packet);

				System.out.println(request + " :: " + response);
			} catch (SocketTimeoutException e) {
				// System.out.println(e);
			}

			// BEGIN --- service advertisement every 1 second
			long currentTime = System.currentTimeMillis();

			elapsedTime += currentTime - prevTime;
			prevTime = currentTime;

			if (elapsedTime >= 1000) {
				elapsedTime -= 1000;

				String advertisement = serviceAddressStr + ":"
						+ Integer.toString(servicePort);
				packet = new DatagramPacket(advertisement.getBytes(),
						advertisement.getBytes().length, multicastAddress,
						multicastPort);
				multicastSocket.send(packet);

				System.out.println("multicast: " + multicastAddressStr + " "
						+ multicastPort + ": " + serviceAddressStr + " "
						+ servicePort);
			}
			// END ---service advertisement
		}

		// close server socket
		serverSocket.close();

		// close multicast socket
		multicastSocket.close();
		
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
	
	public static String getIPv4() {
		System.setProperty("java.net.preferIPv4Stack", "true");

		String ip = null;

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();

				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}

		return ip;
	}
}
