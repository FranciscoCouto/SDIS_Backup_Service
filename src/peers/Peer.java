package peers;

import communication.Send;
import protocols.Backup;
import utilities.Tools;

public class Peer {
	
	static String multicastAddressStr;
	static int servicePort = 8080;
	static String serviceAddressStr;
	static int multicastPort;
	
	public Peer(int port, String IP){	
		
	}
	
	public static void peermain() {
			
		Send se = new Send("225.0.0.0", Tools.getIPv4(), 8000);
		
		String path = Tools.getFile();
		Backup back = new Backup(path);
		
		back.start();
		
		
	}
	
	

}
