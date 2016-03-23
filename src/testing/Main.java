package testing;

import java.io.IOException;

import peers.Peer;

public class Main {
	
	public static void main(String[] args) throws IOException{
		
		if(args.length <3 || args.length > 4){
			System.out.println("Usage:");
			System.out.println("\tjava Main <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			System.exit(0);}

		String[] peer_ap = args[0].split(":");
		System.out.println("IP: " + peer_ap[0]);
		System.out.println("Port: " + peer_ap[1]);
		
		System.out.println("This Peer is now Listening");
		
		Peer.Listen();
		
		
		
		
	}

}
