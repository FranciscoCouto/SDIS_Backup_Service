package testing;

import java.io.IOException;

import peers.Peer;

/**
 * 
 * * > dir /s /B *.java > sources.txt
 *	> javac @sources.txt
 *
 */

public class Main {

	private static String IPv4Address; //definido pela função
	private static int UDPPort; //passado como argumento
	private static String multicastAddress; //endereço multicast
	private static String protocol;
	
	public static void main(String[] args) throws IOException {

		if (!validArgs(args)) {
			System.exit(0);
		}
		
		Peer p = new Peer(UDPPort, multicastAddress,IPv4Address, protocol);
		
		p.logic();
				
		System.out.println("System Exiting!");
		
		System.exit(0);
	}

	//java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>  java TestApp 1923 BACKUP test1.pdf 3
	private static boolean validArgs(String[] args) {
		if (args.length != 4) {

			System.out.println("Usage:");
			System.out
					.println("\tjava Main <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			return false;

		} else {

			String[] peer_ap = args[0].split(":");
			System.out.println("IP: " + peer_ap[0]);
			multicastAddress = peer_ap[0];
			System.out.println("Port: " + peer_ap[1]);
			UDPPort = Integer.valueOf(peer_ap[1]);

			if(args[1].toLowerCase().matches("backup|restore|reclaim|delete"))
				protocol = args[1].toLowerCase();
			else{
				System.out.println("Enter a valid protocol!");
				return false;
			}
			
			return true;
		}
	}

}