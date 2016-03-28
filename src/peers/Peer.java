package peers;

import communication.Control;
import communication.ReceiveBackup;
import communication.ReceiveRestore;
import protocols.Backup;
import protocols.Restore;
import utilities.Tools;

public class Peer {
	
	static String multicastIP;
	static int UDPPort;
	static String IPv4A;
	static int MCControl = 8888, MCBackup = 8887, MCRestore = 8889;
	String protocol;
	private String PeerID;
	
	
	public Peer(int port, String MIP, String IPv4, String type){	
		
		multicastIP = MIP;
		UDPPort = port;
		IPv4A = IPv4;
		protocol = type;
		PeerID = IPv4;
	}
	
	public String getPeerID() {
		return PeerID;
	}
	
	public void logic() {
		
		System.out.println("Initializing Control Channel");
		Control control = new Control(MCControl,multicastIP);
		control.start();
		
		ReceiveRestore restore = new ReceiveRestore(multicastIP,MCRestore);
		restore.start();
		
		ReceiveBackup backup = new ReceiveBackup(multicastIP,MCBackup);
		backup.start();
		
		
		String path = Tools.getFile();
		
		switch(protocol.toLowerCase()){
		
		case "backup":
			
			System.out.println("Initializing Backup Channel");

			
			int deg = Tools.getDeg();
			
			Backup back = new Backup(path, deg, multicastIP, IPv4A, MCBackup, PeerID,control);
			back.start();
			
			break;			
			
		case "restore":
			
			System.out.println("Initializing Restore Channel");

			Restore rest = new Restore(path, multicastIP, IPv4A, MCRestore, PeerID);
			rest.start();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "delete":
			break;
			
		case "reclaim":
			break;
			
		default:
			System.out.println("Unknown Error");
			System.exit(0);
		}	
		
	}
	
	

}
