package peers;

import java.util.ArrayList;
import java.util.List;

import communication.Control;
import communication.Receive;
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
		
		Receive backup = new Receive(UDPPort,multicastIP,IPv4A,MCBackup);
		backup.start();
		
		Receive restore = new Receive(UDPPort,multicastIP,IPv4A,MCRestore);
		restore.start();
		
		switch(protocol.toLowerCase()){
		
		case "backup":
			
			System.out.println("Initializing Backup Channel");

			String path = Tools.getFile();
			int deg = Tools.getDeg();
			
			Backup back = new Backup(path, deg, multicastIP, IPv4A, MCBackup, PeerID,control);
			back.start();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;			
			
		case "restore":
			
			System.out.println("Initializing Restore Channel");

			Restore rest = new Restore("aa.txt", multicastIP, IPv4A, MCRestore, PeerID);
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
