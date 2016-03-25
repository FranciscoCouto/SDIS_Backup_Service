package utilities;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Scanner;

public class Tools {
		
	public static void ChunkFile(){

	}
	
	public static String getIPv4() {
		System.setProperty("java.net.preferIPv4Stack", "true");

		String ip = null;

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();

				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}

		return ip;
	}
	
	public static String sha256(String base) {
		
        try{
            MessageDigest mdigest = MessageDigest.getInstance("SHA-256");
            byte[] result = mdigest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < result.length; i++) {         	
                String hex = Integer.toHexString(0xff & result[i]);
                
                if(hex.length() == 1) 
                	hexString.append('0');
                hexString.append(hex);
            }

        return hexString.toString();
    } catch(Exception ex){
       throw new RuntimeException(ex);
    }
}
	
	public static String getFile() {

		boolean found = false;

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		String path ="", nameFile="";
		
		while(true) {
			System.out.println("Enter the path to the file: ");
			path = in.nextLine();
	
			System.out.println("Enter the name of the file: ");
			nameFile = in.nextLine();
	
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			
			
			if (folder.exists()) {
				for (File file : listOfFiles) {
					//System.out.println(file.getName());
					if (file.isFile() && file.getName().equals(nameFile)) {
						found = true;
						break;
					}
				}
			}
	
			else {
	
				System.out.println("Enter a valid path!");
	
			}
	
			if (found) {
				//return path + nameFile;
				System.out.println("PATH do FCIHEIRO:  " + path+"\\"+nameFile);
				return path+"\\"+nameFile;
			}
			else {
	
				System.out.println("Could not find the file! Try again....");
			}
			
		}
	}

	private static int getDeg() {

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		int repDeg;

		System.out.println("Enter the replication degree: ");
		repDeg = Integer.valueOf(in.nextLine());

		return repDeg;
	}	

}
