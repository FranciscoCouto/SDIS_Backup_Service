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
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Tools {
		
	/**
	 * Responsible to generate a unique bit string (using filename and peerid) through SHA-256
	 * @param s
	 * @return
	 */
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

	/**
	 * Extract header from the message
	 * @param packet
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String[] convertHeader(byte[] packet) throws UnsupportedEncodingException{
		
		String str = new String(packet);
		
		String[] content = (str.split("\r\n\r\n"))[0].split("\\s+");
		
		return content;
		
	}
	
	/**
	 * Extract the body from the message
	 * @param packet
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int convertBody(byte[] packet) throws UnsupportedEncodingException{
		
		int count=0;
		
		
		//Searches for the <CRLF><CRLF> that represents the end of the message. <CRLF> is 0xD0xA 
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
	/**
	 * Responsible for create a message putchunk and return in bytes
	 * @param ChunkNo
	 * @param Version
	 * @param PeerID
	 * @param replicationDeg
	 * @param data
	 * @param FileID
	 * @return
	 */
	public static byte[] CreatePUTCHUNK(int ChunkNo, String Version, String PeerID, int replicationDeg, byte[] data, String FileID){
	
	String BuildMessage = "PUTCHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
			+ ChunkNo + " " + replicationDeg + " " + "\r" + "\n" + "\r" + "\n";  
			
	byte[] AllInBytes = new byte[BuildMessage.getBytes().length + data.length];
	System.arraycopy(BuildMessage.getBytes(), 0, AllInBytes, 0, BuildMessage.getBytes().length);
	System.arraycopy(data, 0, AllInBytes, BuildMessage.getBytes().length, data.length);
	return  AllInBytes;		
}

	/**
	 * Responsible for create a message getchunk and return in bytes
	 * @param ChunkNo
	 * @param Version
	 * @param PeerID
	 * @param FileID
	 * @return
	 */
	public static String CreateGETCHUNK(int ChunkNo, String Version, String PeerID, String FileID){
		
		String BuildMessage = "GETCHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	/**
	 * Responsible for create a message createchunk and return in bytes
	 * @param ChunkNo
	 * @param Version
	 * @param PeerID
	 * @param data
	 * @param FileID
	 * @return
	 */
	public static byte[] CreateCHUNK(int ChunkNo, String Version, String PeerID, byte[] data, String FileID){
		
		String BuildMessage = "CHUNK" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		byte[] AllInBytes = new byte[BuildMessage.getBytes().length + data.length];
		System.arraycopy(BuildMessage.getBytes(), 0, AllInBytes, 0, BuildMessage.getBytes().length);
		System.arraycopy(data, 0, AllInBytes, BuildMessage.getBytes().length, data.length);
		return  AllInBytes;			
	}
	
	/**
	 * Responsible for create a message delete and return in bytes
	 * @param Version
	 * @param PeerID
	 * @param FileID
	 * @return
	 */
	public static String CreateDelete(String Version, String PeerID, String FileID){
		
		String BuildMessage = "DELETE" + " " + Version + " " + PeerID + " " + FileID + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}

	/**
	 * Responsible for create a message removed and return in bytes
	 * @param Version
	 * @param PeerID
	 * @param FileID
	 * @param ChunkNo
	 * @return
	 */
	public static String CreateRemoved(String Version, String PeerID, String FileID, String ChunkNo){
		
		String BuildMessage = "REMOVED" + " " + Version + " " + PeerID + " " + FileID + " " + ChunkNo + " " + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	/**
	 * Responsible for remove one line from map.txt. 
	 * @param file
	 * @param lineToRemove
	 * @throws IOException
	 */
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
	 
	/**
	 * Responsible for remove all files with that fileID
	 * @param fileId
	 * @return
	 */
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
	
	/**
	 * Get the last modified file 
	 * @param dir
	 * @return
	 */
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
	/**
	 * Responsible for create a message stored 
	 * @param ChunkNo
	 * @param Version
	 * @param PeerID
	 * @param FileID
	 * @return
	 */
	public static String CreateSTORED(int ChunkNo, String Version, String PeerID, String  FileID){
		
		String BuildMessage = "STORED" + " " + Version + " " + PeerID + " " + FileID + " "
				+ ChunkNo + "\r" + "\n" + "\r" + "\n";  
				
		return BuildMessage;		
	}
	
	/**
	 * Responsible for split the file beetween 64000*chunkNo and (64000*chunkNo) + 64000 or if last chunk, less
	 * @param path
	 * @param chunkNo
	 * @param size
	 * @return
	 */
	public static byte[] splitfile(Path path, int chunkNo, int size){
		
		byte fileContent[] = null;
       
		try {
			fileContent = Files.readAllBytes(path); //Read the content of the file 
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
		byte chunk[] = Arrays.copyOfRange(fileContent, 64000*chunkNo, (64000*chunkNo)+size);
		
        return chunk;

	}
	
	/**
	 * Gets a random number beetween min and max
	 * @param min
	 * @param max
	 * @return
	 */
	public static int random(int min, int max) {
	    Random rand = new Random();
	    int Num = rand.nextInt((max - min) + 1) + min;
	
	    return Num;
	}
	
	/**
	 * Responsible for save map with file id and total number of chunks
	 * @param FileID
	 * @param ChunkID
	 * @throws IOException
	 */
	public static void saveMap(String FileID, int ChunkID, String PeerID) throws IOException {

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
		bw.write(FileID+" "+ String.valueOf(ChunkID) + " " + PeerID);
		bw.write("\r\n");
		bw.close();
	}

	/**
	 * Get the Chunk Number from the map
	 * @param fileID
	 * @return
	 */
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
		       if((testLine[0]+".bak").equals(fileID)){
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
	
	/**
	 * Responsible for save body of chunks in peers 
	 * @param chunkNo
	 * @param fileID
	 * @param body
	 * @throws IOException
	 */
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
	
	/**
	 * Responsible for create Restore File and save body
	 * @param chunkNo
	 * @param fileID
	 * @param body
	 * @param filepath
	 * @throws IOException
	 */
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
	
		 //convert array of bytes into file
	    FileOutputStream fileOuputStream = 
                  new FileOutputStream(file,true); 
	    //System.out.println("BODYYY: " + body);
	    fileOuputStream.write(body);
	    fileOuputStream.close();
	}
	
	/**
	 * Responsible for remove the trash from message
	 * @param message
	 * @param init
	 * @return
	 */
	public static byte[] trim (byte[] message, int init){
		
		int i=message.length-1;
		
		//extrai aqui o que interessa
		while(i>=0 && message[i]==0){
			--i;
		}
		return Arrays.copyOfRange(message,init,i+1);
		
	}
	
	public static void saveFIDCKNO(String ChunkNo, String fileID) throws IOException {

		File dir = new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator);
		
		if (!dir.exists()) {
			   dir.mkdirs();
		}
		
		File file = new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator+ "Desired.txt");
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fileID + " " + ChunkNo);
		bw.write("\r\n");
		bw.close();
	}
	
	/**
	 * Responsible to save RepDeg for a certain fileID removing duplicates
	 * @param ChunkNo
	 * @param fileID
	 * @throws IOException
	 */
	public static void saveRep(String Repdeg, String fileID)
			throws IOException {

		File dir = new File(System.getProperty("user.dir") + File.separator
				+ "Rep" + File.separator);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(System.getProperty("user.dir") + File.separator
				+ "Rep" + File.separator + "Rep.txt");

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true); //Responsible for writing
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fileID + " " + Repdeg);
		bw.write("\r\n");
		bw.close();

		BufferedReader reader = new BufferedReader(new FileReader(file)); //From here down it's responsible to filter duplicates
		Set<String> lines = new HashSet<String>(10000); // maybe should be
														// bigger
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		reader.close();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (String unique : lines) {
			writer.write(unique);
			writer.newLine();
		}
		writer.close();
	}
	
	/**
	 * Responsible for save disk size in peerid.txt
	 * @param DiskSpaceMax
	 * @param PeerID
	 * @throws IOException
	 */
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
	
	/**
	 * gets size of disk 
	 * @param PeerID
	 * @return
	 */
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
	
	/**
	 * Responsible for change disk size in txt, when its backup increase and when its delete decrease
	 * @param protocol
	 * @param size
	 * @param PeerID
	 */
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
	
	/**
	 * Responsible for remove file from folder 
	 * @param filepath
	 */
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
	
	public static int getRealRep(String fileID,String ChunkNo){
		
		File file =new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator+ "Desired.txt");
		
		int RealRep = 0;
		try{ 
   
    		if(file.exists()){
    		@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
		    //Procura nas linhas do ficheiro ate encontrar uma relativa ao fileid
    		String line;
		    while ((line = br.readLine()) != null) {
		       String[] testLine=line.split("\\s+");
		       if((testLine[0]+".bak").equals(fileID) && testLine[1].equals(ChunkNo)){
		    	   //System.out.println("HEEROOOO: "+ Integer.parseInt(test[1]));
		    	   RealRep++;
		       }
		    }
		}
		}catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return RealRep;
	}
	
	/**
	 * Get the Chunk Number from the rep.txt
	 * @param fileID
	 * @return
	 */
	public static int getChunkNoRep(String fileID){
		
		File file =new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator+ "Rep.txt");
		try{ 
   
    		if(file.exists()){
    		@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
		    //Procura nas linhas do ficheiro ate encontrar uma relativa ao fileid
    		String line;
		    while ((line = br.readLine()) != null) {
		       String[] testLine=line.split("\\s+");
		       if((testLine[0]+".bak").equals(fileID)){
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
	
	public static void removeLine(String lineToRemove) throws IOException {
		
		File inputFile = new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator+ "Desired.txt");
		File tempFile = new File(System.getProperty("user.dir") + File.separator + "Rep" + File.separator+ "DesiredTemp.txt");
	
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		int count=0;
		String currentLine;
	
		while((currentLine = reader.readLine()) != null) {
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.equals(lineToRemove) && count == 0){count++; continue;}
		    writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close(); 
		reader.close(); 
		tempFile.renameTo(inputFile);
	}
	
	/**
	 * Get the PeerID from the map
	 * @param fileID
	 * @return
	 */
	public static String getPeerID(String fileID){
		
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
		    	   return testLine[2];
		       }
		    }
		}
		}catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return "";
	}
}