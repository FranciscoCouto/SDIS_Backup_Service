package peers;

import communication.Receive;
import protocols.Backup;
import utilities.Tools;

public class Peer {
	
	static String multicastIP;
	static int UDPPort = 8080;
	static String IPv4A;
	static int MCControl = 8888, MCBackup = 8887, MCRestore = 8889;
	String protocol;
	
	public Peer(int port, String MIP, String IPv4, String type){	
		
		multicastIP = MIP;
		UDPPort = port;
		IPv4A = IPv4;
		protocol = type;
	}
	
	public void logic() {
			
		System.out.println("Initializing Control Channel");
		Receive control = new Receive(UDPPort,multicastIP,IPv4A,MCControl);
		control.start();
		
		switch(protocol.toLowerCase()){
		
		case "backup":
			String path = Tools.getFile();
			System.out.println("Initializing Backup Channel");
			
			Receive backup = new Receive(UDPPort,multicastIP,IPv4A,MCBackup);
			backup.start();
			
			Backup back = new Backup(path, multicastIP, IPv4A, MCBackup);
			back.start();

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
