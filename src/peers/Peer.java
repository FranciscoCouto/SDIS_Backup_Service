package peers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import protocols.Backup;
import protocols.Delete;
import protocols.Restore;
import protocols.Reclaiming;
import communication.Control;
import communication.ReceiveBackup;
import communication.ReceiveRestore;

public class Peer {
	
	static String multicastIPControl,multicastIPBackup,multicastIPRestore;
	static int TCPPort;
	static int MCControl, MCBackup, MCRestore;
	static String protocol;
	private static String PeerID;
	private static ServerSocket socket;
	
	
	
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
		
		 boolean done = false;
		 
	        while (!done) {
	            Socket connection = null; //Baseado em http://www.tutorialspoint.com/java/java_networking.htm
	            try {
	            	connection = socket.accept();
	            } catch (Exception e) { e.printStackTrace(); }

	            try {
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                String protocol = br.readLine();
	                String[] testappinput = protocol.split(";");
	                System.out.println("I want this protocol says test app: " + testappinput[0].toUpperCase());

	                switch(testappinput[0].toLowerCase()){
	        		
	        		case "backup":
	        			
	        			System.out.println("Initializing Backup Channel");

	        			Backup back = new Backup(testappinput[1], Integer.valueOf(testappinput[2]), multicastIPBackup, MCBackup, PeerID, control);
	        			back.start();
	        			
	        			break;			
	        			
	        		case "restore":
	        			
	        			System.out.println("Initializing Restore Channel");
	        			
	        			Restore rest = new Restore(testappinput[1], multicastIPRestore, MCRestore, PeerID, control);
	        			rest.start();
	        			
	        			break;
	        			
	        		case "delete":
	        			
	        			System.out.println("Initializing Delete Channel");

	        			Delete del = new Delete(testappinput[1], multicastIPControl, MCControl, PeerID); //AQUI PASSAMOS OS DADOS DO CANAL DE CONTROLO CONFIRMAR
	        			del.start();
	        			
	        			break;
	        			
	        		case "reclaim":
	        			
	        			System.out.println("Initializing Reclaim Channel");
	        			
	        			Reclaiming rec = new Reclaiming(multicastIPControl, MCControl, PeerID); //AQUI PASSAMOS OS DADOS DO CANAL DE CONTROLO CONFIRMAR
	        			rec.start();
	        			
	        			break;
	        			
	        		default:
	        			System.out.println("Unknown Error");
	        			System.exit(0);
	        		}	
	                
	                
	            } catch (Exception e) { e.printStackTrace(); }
	        }
	        
	        
	        System.out.println("Closing peer with ID " + PeerID);
	        try { 
	        	socket.close(); } 
	        catch (IOException e) 
	        { 
	        	e.printStackTrace();}
	        
	        System.exit(0);
			
	}
	

	public static void main(String[] args) {
		
		if(args.length != 5)
		{
			/*
			 * Correr: java peers.Peer 123 6000 225.0.0.3:8888 225.0.0.3:8887 225.0.0.3:8889 
			 */
			System.out.println("Usage: java Peer <PeerID> <TCPport> <mcIP>:<mcPORT> <mdbIP>:<mdbPORT> <mdrIP>:<mdrPORT>");
			System.exit(1);
		}
		
		PeerID = args[0];
		TCPPort = Integer.valueOf(args[1]);
		
		String[] MCC = args[2].split(":");
		String[] MDB = args[3].split(":");
		String[] MDR = args[4].split(":");
		
		multicastIPControl = MCC[0];
		MCControl = Integer.parseInt(MCC[1]);
		multicastIPBackup = MDB[0];
		MCBackup = Integer.parseInt(MDB[1]);
		multicastIPRestore = MDR[0];
		MCRestore = Integer.parseInt(MDR[1]);
		
		try {
            socket = new ServerSocket(TCPPort);
        } catch (Exception e){
        	e.printStackTrace(); 
        }

		System.out.println("Initialized peer with ID :" + PeerID);
		System.out.println("TCP Socket open on port " + socket.getLocalPort());
        
        
		logic();
		
		System.out.println("<Press any key to stop executing>");
		try {
			System.in.read();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	
		
		System.out.println("SHUTING DOWN!");
		System.exit(0);
	}
	

}
