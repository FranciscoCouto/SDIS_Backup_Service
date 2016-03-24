package testing;

import java.io.IOException;

import communication.Receive;
import communication.Send;
import peers.Peer;
import utilities.Tools;

/**
 * 
 * * > dir /s /B *.java > sources.txt
 *	> javac @sources.txt
 *
 */

public class Main {

	/*private static String IPv4Address;
	private static int UDPPort;

	private static String multicastAddress;
	private static int multicastPort;
*/
	
	public static void main(String[] args) throws IOException {

		//if (!validArgs(args)) {
			//System.exit(0);
		//}
		
		//Receive re = new Receive(8080,"225.0.0",Tools.getIPv4(),8000);
		
		//re.start();
		
		Peer.peermain();
				
	}

	/*
	private static boolean validArgs(String[] args) {
		if (args.length != 4) {

			System.out.println("Usage:");
			System.out
					.println("\tjava Main <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			return false;

		} else {

			String[] peer_ap = args[0].split(":");
			System.out.println("IP: " + peer_ap[0]);
			System.out.println("Port: " + peer_ap[1]);

			return true;
		}
	}*/

}