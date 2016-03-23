package testing;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import peers.Peer;
import utilities.Tools;

public class Main {
	
	private static String IPv4Address;
	private static int UDPPort;

	private static String multicastAddress;
	private static int multicastPort;
	
	public static void main(String[] args) throws IOException{

		
		
		System.out.println("This Peer is now Listening");
		
		if(args[0].equals("Server")){
			//Peer.Listen();	
		}
		
		else{
			switch(args[1].toLowerCase()){
			
			case "backup":
				
			case "restore":
				
			case "delete":
				
			case "reclaim":
			
			}
			
			//Peer.start();	
		}

	}
	
	private static boolean validArgs(String[] args){
		if (args.length != 4) {
			
			System.out.println("Usage:");
			System.out.println("\tjava Main <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			return false;
			
		} else {
			
			String[] peer_ap = args[0].split(":");
			System.out.println("IP: " + peer_ap[0]);
			System.out.println("Port: " + peer_ap[1]);
			
			/*IPv4Address;
			UDPPort;

			multicastAddress;
			multicastPort;*/

			return true;
		}
	}

	public static void findFile(String name,File file)
    {
    }
	
    public static void getFile() 
    {
        
    }

	private static void getDeg(){
		
	}
	
}