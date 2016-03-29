package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

public class Tools {
		
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
           // System.out.println("OLE   " + hexString.toString());
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
					if (file.getName().equals(nameFile)) {
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

	public static int getDeg() {

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		int repDeg;

		System.out.println("Enter the replication degree: ");
		repDeg = Integer.valueOf(in.nextLine());

		return repDeg;
	}
	
	public static String[] convertHeader(byte[] packet) throws UnsupportedEncodingException{
		
		String str = new String(packet, "UTF-8");
		
		String[] content = (str.split("\r\n\r\n"))[0].split("\\s+");
		
		return content;
		
	}
	
	public static String convertBody(byte[] packet) throws UnsupportedEncodingException{
		
		
		String str = new String(packet, "UTF-8");

		String[] content = (str.split("\r\n\r\n"));
		
		return content[1];
		
	}

	/**
	 * PUTCHUNK
	 * <version>
	 * <SenderID>
	 * <FieldID>
	 * <ChunkNo>
	 * <ReplicationDeg>
	 * <CRLF>
	 * <CRLF>
	 * <Body>
	 */
	public static String CreatePUTCHUNK(int ChunkNo, String Version, String PeerID, int replicationDeg, String data, String FileID){
		
		String BuildMessage = "PUTCHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + replicationDeg + " " + "\r" + "\n" + "\r" + "\n" + data;  
				
		return BuildMessage;		
	}

	public static String CreateGETCHUNK(int ChunkNo, String Version, String PeerID, String FileID){
		
		String BuildMessage = "GETCHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	public static String CreateCHUNK(int ChunkNo, String Version, String PeerID, String data, String FileID){
		
		String BuildMessage = "CHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + "\r" + "\n" + "\r" + "\n" + data;  
				
		return BuildMessage;		
	}
	
	public static String CreateDelete(String Version, String PeerID, String FileID){
		
		String BuildMessage = "DELETE" + " " + Version + " " + PeerID + " " + FileID + " " + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	 public static void removeLineFromFile(String file, String lineToRemove) {

		    try {

		      File inFile = new File(file);

		      if (!inFile.isFile()) {
		        System.out.println("Parameter is not an existing file");
		        return;
		      }

		      //Construct the new file that will later be renamed to the original filename.
		      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

		      BufferedReader br = new BufferedReader(new FileReader(file));
		      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

		      String line = null;

		      //Read from the original file and write to the new
		      //unless content matches data to be removed.
		      while ((line = br.readLine()) != null) {

		        if (!line.trim().equals(lineToRemove)) {

		          pw.println(line);
		          pw.flush();
		        }
		      }
		      pw.close();
		      br.close();

		      //Delete the original file
		      if (!inFile.delete()) {
		        System.out.println("Could not delete file");
		        return;}
		    } catch(IOException e) {
		        // ... handle errors ...
		    }    
	 }

	 public static void removeFiles(String fileId) {
		 
		 File dir = new File("C:\\SDIS\\Chunks\\");
		 
		 for(File file: dir.listFiles()) 
			 if(file.getName().matches(".* - "+fileId))
				 file.delete();
	 }
	
	/**
	 * PUTCHUNK
	 * <version>
	 * <SenderID>
	 * <FieldID>
	 * <ChunkNo>
	 * <ReplicationDeg>
	 * <CRLF>
	 * <CRLF>
	 * <Body>
	 */
	public static String CreateSTORED(int ChunkNo, String Version, String PeerID, String  FileID){
		
		String BuildMessage = "STORED" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	public static byte[] splitfile(Path path, int chunkNo, int size){
		
		byte fileContent[] = null;
       
		try {
			fileContent = Files.readAllBytes(path); /** Read the content of the file */
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
        
		
		byte chunk[] = Arrays.copyOfRange(fileContent, 64000*chunkNo, (64000*chunkNo)+size);
		
		
        return chunk;

	}

	public static int random(int min, int max) {
	    Random rand = new Random();
	    int Num = rand.nextInt((max - min) + 1) + min;
	
	    return Num;
	}
	
	public static void saveMap(String FileID, int ChunkID) throws IOException {

		File dir = new File("C:\\SDIS\\Map\\");
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File("C:\\SDIS\\Map\\Map.txt");
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(FileID+" "+ String.valueOf(ChunkID));
		bw.close();
	}

	public static int getChunkNo(String fileID){
		
		File file =new File("C:\\SDIS\\Map\\Map.txt");
		try{ 
   
    		if(file.exists()){
    		@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
		    /**
		     * Searches through the lines of the file until finding the one relative to the FileId
		     */
    		String line;
		    while ((line = br.readLine()) != null) {
		       String[] test=line.split("\\s+");
		       if(test[0].equals(fileID)){
		    	   System.out.println("HEEROOOO: "+ Integer.parseInt(test[1]));
		    	   return Integer.parseInt(test[1]);
		       }
		    }
		}
		}catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void SaveChunks( String chunkNo, String fileID, String body) throws IOException {
		File dir = new File("C:\\SDIS\\Chunks\\");
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File("C:\\SDIS\\Chunks\\"+chunkNo+"-"+fileID);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(body);
		bw.close();
	}
	
	public static void RestoreFile( String chunkNo, String fileID, String body) throws IOException {
		File dir = new File("C:\\SDIS\\Restore\\");
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File("C:\\SDIS\\Restore\\ficheiro.txt"); //ALTERAR PARA SER DIFERENTE
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(body);
		bw.close();
	}
}