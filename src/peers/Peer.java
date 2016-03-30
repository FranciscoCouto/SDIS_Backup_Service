package peers;

import java.io.IOException;

import communication.Control;
import communication.ReceiveBackup;
import communication.ReceiveRestore;

public class Peer {
	
	static String multicastIPControl,multicastIPBackup,multicastIPRestore;
	static int UDPPort; //PARA QUE VAMOS USAAAR?????
	static int MCControl, MCBackup, MCRestore;
	static String protocol;
	private static String PeerID;
	public static long DiskSpaceMax = Long.parseLong("500000000"); //500 mb
	public static long SpaceOccupied = 0;
	
	public String getPeerID() {
		return PeerID;
	}
	
	public static void logic() {
		
		System.out.println("Initializing Control Channel");
		Control control = new Control(MCControl,multicastIPControl,0);
		control.start();
		
		ReceiveRestore restore = new ReceiveRestore(multicastIPRestore,MCRestore,multicastIPControl,MCControl);
		restore.start();
		
		ReceiveBackup backup = new ReceiveBackup(multicastIPBackup,MCBackup,multicastIPControl,MCControl,PeerID);
		backup.start();
			
	}
	

	public static void main(String[] args) {
		
		//-------------------------
		// Checking Arguments
		//-------------------------
		if(args.length != 5)
		{
			/*
			 * Example Args: UniqueID 50123 224.224.224.224:15000 224.224.224.225:15001 224.225.226.232:12345
			 */
			
			System.out.println("Call Error: Wrong number of arguments");
			System.out.println("----------------------------");
			System.out.println("Usage: java Peer <PeerID> <UDPport> <mcIP>:<mcPORT> <mdbIP>:<mdbPORT> <mdrIP>:<mdrPORT>");
			System.out.println("<TCPport> - Port on which the TestApp should connect to (leave as 0 for random ports)");
			System.out.println("<PeerID> - name by which this ID is recognized, should be unique");
			System.out.println("<IP>:<PORT> - Multicast channel addresses for MC, MDB and MDR");
			System.exit(1);
		}
		
		//-------------------------
		// Initialising variables
		//-------------------------
		
		PeerID = args[0];
		
		UDPPort = Integer.valueOf(args[1]);
		
		String[] MCArgs = args[2].split(":");
		String[] MDBArgs = args[3].split(":");
		String[] MDRArgs = args[4].split(":");
		
		multicastIPControl = MCArgs[0];
		MCControl = Integer.parseInt(MCArgs[1]);
		multicastIPBackup = MDBArgs[0];
		MCBackup = Integer.parseInt(MDBArgs[1]);
		multicastIPRestore = MDRArgs[0];
		MCRestore = Integer.parseInt(MDRArgs[1]);
		
		logic();
		
		
		//-------------------------
		// Instructions for stopping and Termination
		//-------------------------
		
		System.out.println("<Press any key to stop executing>");
		try {System.in.read();} 
			catch (IOException e) {e.printStackTrace();}
	
		
		System.out.println("Closing down.");
		System.exit(0);
	}
	

}
