package peers;

import communication.Control;
import communication.Receive;
import protocols.Backup;
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
		Control control = new Control(UDPPort,multicastIP,IPv4A,MCBackup);
		control.start();
		
		switch(protocol.toLowerCase()){
		
		case "backup":
			
			System.out.println("Initializing Backup Channel");
			
			Receive backup = new Receive(8888,multicastIP,IPv4A,MCBackup);
			backup.start();
			
			
			String path = Tools.getFile();
			//System.out.println("AQUIII " + path);
			Backup back = new Backup(path, multicastIP, IPv4A, MCBackup, PeerID);
			back.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;			
			
		case "restore":
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
