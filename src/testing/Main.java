package testing;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 
 * > dir /s /B *.java > sources.txt
 * > javac @sources.txt
 * > java testing.Main 127.0.0.1:6000 backup teste.txt 1
 *
 */

public class Main {

	private static String IP;
	private static int PORT;
	private static String FilePath;
	private static int RepDeg;
	private static String protocol;
	
	public static void main(String[] args) throws IOException {

		if (!validArgs(args)) {
			System.out.println("EXITING!!");
			System.exit(0);
		}
		
		try{
			
			 InetAddress addr = InetAddress.getByName(IP);

             Socket socket = new Socket(addr, PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             
             String request = "";
             if (protocol.toLowerCase().equals("backup")) {
                 request = "BACKUP" + ";" + FilePath + ";" + RepDeg;
             }
             else if (protocol.toLowerCase().equals("restore")) {
                 request = "RESTORE" + ";" + FilePath;
             }
             else if (protocol.toLowerCase().equals("delete")) {
                 request = "DELETE" + ";" + args[2];
             }
             else if (protocol.toLowerCase().equals("reclaim")) {
                 request = "RECLAIM" + ";" + args[2];
             }
             else {
            	 
            	 System.out.println("ERROR IN TESTAPP ARGUMENTS - QUITING");
            	 System.exit(0);
            	 
             }
             
             System.out.println("Socket on "+IP+" : "+PORT);
             System.out.println("Sending request: " + request);
             out.writeBytes(request);
             socket.close();

		} catch (ConnectException e) { System.err.println("Couldn't connect to peer on " + IP+" : " + PORT);
	    } catch (Exception e) { e.printStackTrace(); }
	}

	private static boolean validArgs(String[] args) {
		
		
		String[] peer_ap = args[0].split(":");
		System.out.println("IP: " + peer_ap[0]);
		IP = peer_ap[0];
		System.out.println("Port: " + peer_ap[1]);
		PORT = Integer.valueOf(peer_ap[1]);
		
		protocol = args[1].toLowerCase();
		
		switch(protocol){
		
		case "backup":
			if (args.length != 4) { 

				System.out.println("USAGE:");
				System.out
						.println("\tjava Main TCPIP:TCPPORT BACKUP FILENAME REPDEG");
				return false;

			} else {
				FilePath = System.getProperty("user.dir") + File.separator + "Files" + File.separator + args[2]; 
				RepDeg = Integer.valueOf(args[3]);
				return true;
			}
			
		case "restore":
			if (args.length != 3) { 

				System.out.println("USAGE:");
				System.out
						.println("\tjava Main TCPIP:TCPPORT RESTORE FILENAME");
				return false;

			} else {
				FilePath = System.getProperty("user.dir") + File.separator + "Files" + File.separator + args[2]; 
				return true;
			}
			
		case "delete":
			if (args.length != 3) { 

				System.out.println("USAGE:");
				System.out
						.println("\tjava Main TCPIP:TCPPORT DELETE FILENAME");
				return false;

			} else {
				FilePath = System.getProperty("user.dir") + File.separator + "Files" + File.separator + args[2];
				return true;
			}
			
		case "reclaim":
			if (args.length != 3) { 

				System.out.println("USAGE:");
				System.out
						.println("\tjava Main TCPIP:TCPPORT RECLAIM DISKSPACE");
				return false;

			} else {
				FilePath = System.getProperty("user.dir") + File.separator + "Files" + File.separator + args[2];
				return true;
			}
			
		default:
			System.out.println("ENTER A VALID PROTOCOL! REPEAT WITH BACKUP|RESTORE|DELETE|RECLAIM");
			return false;

		}

	}

}