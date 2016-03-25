package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class ControlBackup extends Thread{
	
	private static int PORT;
	private static String ADDR;
	private static int PORTCONTROL;
	private static String ADDRCONTROL;

	public ControlBackup(int servicePort, String multicastAddressStr,String serviceAddressStr, int multicastPort){
		PORT=servicePort;
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
		ADDRCONTROL=serviceAddressStr;
	}

	
	@Override
	public void run() {
		System.out.println("asda");
		
		try(MulticastSocket multicastSocket = new MulticastSocket();){
		multicastSocket.setTimeToLive(1);

		InetAddress multicastAddress = InetAddress
				.getByName(ADDR);

		// open server socket
		DatagramSocket serverSocket = new DatagramSocket(PORT);
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
				String response = "Packet store";
				
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

				String advertisement = ADDRCONTROL + ":"
						+ Integer.toString(PORT);
				packet = new DatagramPacket(advertisement.getBytes(),
						advertisement.getBytes().length, multicastAddress,
						PORTCONTROL);
				multicastSocket.send(packet);

				//System.out.println("multicast: " + ADDR + " "
					//	+ PORTCONTROL + ": " + ADDRCONTROL + " "
						//+ PORT);
			}
			// END ---service advertisement
		}

		// close server socket
		serverSocket.close();

		// close multicast socket
		multicastSocket.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

}
