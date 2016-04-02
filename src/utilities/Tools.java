package utilities;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Tools {
		
	
	public static String sha256(String s) {
		
        try{
            MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
            byte[] result = messagedigest.digest(s.getBytes("UTF-8"));
            StringBuffer ShaString = new StringBuffer();

            for (int i = 0; i < result.length; i++) {         	
                String hex = Integer.toHexString(0xff & result[i]);
                
                if(hex.length() == 1) 
                	ShaString.append('0');
                ShaString.append(hex);
            }
           // System.out.println("OLE   " + hexString.toString());
        return ShaString.toString();
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
		
		String str = new String(packet);
		
		String[] content = (str.split("\r\n\r\n"))[0].split("\\s+");
		
		return content;
		
	}
	
	
	public static int convertBody(byte[] packet) throws UnsupportedEncodingException{
		
		int count=0;
		
		/**
		 * Searches for the <CRLF><CRLF> that represents the end of the message. <CRLF> is 0xD0xA
		 */
		while(count<packet.length){
			if(packet[count] == (byte)0xD && packet[count+1] == (byte)0xA && packet[count+2] == (byte)0xD && packet[count+3] == (byte)0xA){
				break;
			}
			count++;
		}
		count=count+4;

		return count;
		
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
	public static byte[] CreatePUTCHUNK(int ChunkNo, String Version, String PeerID, int replicationDeg, byte[] data, String FileID){
	
	String BuildMessage = "PUTCHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
			+ ChunkNo + " " + replicationDeg + " " + "\r" + "\n" + "\r" + "\n";  
			
	byte[] AllInBytes = new byte[BuildMessage.getBytes().length + data.length];
	System.arraycopy(BuildMessage.getBytes(), 0, AllInBytes, 0, BuildMessage.getBytes().length);
	System.arraycopy(data, 0, AllInBytes, BuildMessage.getBytes().length, data.length);
	return  AllInBytes;		
}

	public static String CreateGETCHUNK(int ChunkNo, String Version, String PeerID, String FileID){
		
		String BuildMessage = "GETCHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	public static byte[] CreateCHUNK(int ChunkNo, String Version, String PeerID, byte[] data, String FileID){
		
		String BuildMessage = "CHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		byte[] AllInBytes = new byte[BuildMessage.getBytes().length + data.length];
		System.arraycopy(BuildMessage.getBytes(), 0, AllInBytes, 0, BuildMessage.getBytes().length);
		System.arraycopy(data, 0, AllInBytes, BuildMessage.getBytes().length, data.length);
		return  AllInBytes;			
	}
	
	public static String CreateDelete(String Version, String PeerID, String FileID){
		
		String BuildMessage = "DELETE" + " " + Version + " " + PeerID + " " + FileID + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}

	public static String CreateRemoved(String Version, String PeerID, String FileID, String ChunkNo){
		
		String BuildMessage = "REMOVED" + " " + Version + " " + PeerID + " " + FileID + " " + ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	public static void removeLineFromFile(String file, String lineToRemove) throws IOException {

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
		        
		        if (!line.trim().contains(lineToRemove)) {
		 
		          pw.println(line);
		          pw.flush();
		        }
		      }
		      pw.close();
		      br.close();
		      System.gc();
		      
		      System.out.println("nome: " + inFile.getAbsolutePath());
		      
		      //Delete the original file
		      if (!inFile.delete()) {
		        System.out.println("Could not delete file");
		        return;
		      } 
		      
		      //Rename the new file to the filename the original file had.
		      if (!tempFile.renameTo(inFile))
		        System.out.println("Could not rename file");
		      
		    }
		    catch (FileNotFoundException ex) {
		      ex.printStackTrace();
		    }
		    catch (IOException ex) {
		      ex.printStackTrace();
		    }
		 
	 }
	 
	public static long removeFiles(String fileId) {
		 
		 File dir = new File(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator);
		 long sizeRemoved = 0;
		 
		 for(File file: dir.listFiles()) {
			 if(file.getName().matches(".*-"+fileId+".bak")){
				 sizeRemoved += file.length();
				 file.delete();
			 }
		 }
		 
		 return sizeRemoved;
	 }
	
	public static File lastFileModified(String dir) {
	    File fl = new File(dir);
	    File[] files = fl.listFiles(new FileFilter() {          
	        public boolean accept(File file) {
	            return file.isFile();
	        }
	    });
	    long lastMod = Long.MIN_VALUE;
	    File choice = null;
	    for (File file : files) {
	        if (file.lastModified() > lastMod) {
	            choice = file;
	            lastMod = file.lastModified();
	        }
	    }
	    return choice;
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

		File dir = new File(System.getProperty("user.dir") + File.separator + "Map" + File.separator);
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File(System.getProperty("user.dir") + File.separator + "Map" + File.separator+ "Map.txt");
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(FileID+" "+ String.valueOf(ChunkID));
		bw.write("\r\n");
		bw.close();
	}

	public static int getChunkNo(String fileID){
		
		File file =new File(System.getProperty("user.dir") + File.separator + "Map" + File.separator+ "Map.txt");
		try{ 
   
    		if(file.exists()){
    		@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
		    //Procura nas linhas do ficheiro ate encontrar uma relativa ao fileid
    		String line;
		    while ((line = br.readLine()) != null) {
		       String[] testLine=line.split("\\s+");
		       if(testLine[0].equals(fileID)){
		    	   //System.out.println("HEEROOOO: "+ Integer.parseInt(test[1]));
		    	   return Integer.parseInt(testLine[1]);
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
	
	public static void SaveChunks( String chunkNo, String fileID, byte[] body) throws IOException {
		//File dir = new File("C:\\SDIS\\Chunks\\");
		File dir = new File(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator);
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		//File file = new File("C:\\SDIS\\Chunks\\"+chunkNo+"-"+fileID+".bak");
		File file = new File(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator+chunkNo+"-"+fileID+".bak");	
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		
		try {
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
			writer.write(body);
			writer.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	
	}
	
	public static void RestoreFile( String chunkNo, String fileID, byte[] body, String filepath) throws IOException {
		
		File dir = new File(System.getProperty("user.dir") + File.separator + "Restore" + File.separator);
		
		String fileNameMoreExt = new File(filepath).getName();
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File(System.getProperty("user.dir") + File.separator + "Restore" + File.separator+ fileNameMoreExt); 
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		//OutputStream out = null;
		 //out = new  BufferedOutputStream
		 //convert array of bytes into file
	    FileOutputStream fileOuputStream = 
                  new FileOutputStream(file,true); 
	    //System.out.println("BODYYY: " + body);
	    fileOuputStream.write(body);
	    fileOuputStream.close();
	}
	
	public static byte[] trim (byte[] message, int init){
		
		int i=message.length-1;
		
		//extrai aqui o que interessa
		while(i>=0 && message[i]==0){
			--i;
		}
		return Arrays.copyOfRange(message,init,i+1);
		
	}
	
	public static void saveRep(String RepNeeded, String RepDeg, String fileID) throws IOException {
		

		File dir = new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator);
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator+ "Rep.txt");
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fileID+" "+RepDeg+" "+RepNeeded);
		bw.write("\r\n");
		bw.close();
	}
	
	public static void SaveDiskSize(long DiskSpaceMax, String PeerID) throws IOException {
		
		File dir = new File(System.getProperty("user.dir") + File.separator + "DiskSize" + File.separator);
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File(System.getProperty("user.dir") + File.separator + "DiskSize" + File.separator + PeerID + ".txt");
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		PrintWriter out = new PrintWriter(file);
		out.println(DiskSpaceMax);
		out.close();
	}
	
	public static long returnDiskSize(String PeerID) {
			
		String disksize = null;
		try {
			disksize = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "DiskSize" + File.separator + PeerID + ".txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Long.valueOf(disksize).longValue();
	}
	
	public static void ChangeDiskSize(String protocol, long size, String PeerID) {
		
		String disksize = null;
		try {
			disksize = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "DiskSize" + File.separator + PeerID + ".txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long SizeAtual = Long.parseLong(disksize.trim());
		
		switch(protocol.toLowerCase()) {
		
		case "backup":
			SizeAtual -= size;
			break;
		case "delete":
			SizeAtual += size;
			break;
		}
		
		try {
			SaveDiskSize(SizeAtual, PeerID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void RemoveFileFromFolder(String filepath) {
		
		try{    		
    		File file = new File(filepath);
        	
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		
	}
}