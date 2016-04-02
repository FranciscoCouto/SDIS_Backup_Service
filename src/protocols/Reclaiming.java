package protocols;

import java.io.File;
import java.io.IOException;

import utilities.Tools;
import communication.Send;

public class Reclaiming extends Thread{
	
	static String CONTROLIP, Version,PeerID;
	static int  ControlPORT;
	static long diskSize;
	
	/**
	 * Class Constructor
	 * @param ControlIP
	 * @param ControlPort
	 * @param PeerId
	 */
	public Reclaiming(String ControlIP, int ControlPort, String PeerId, long disk){
		
		CONTROLIP = ControlIP;
		ControlPORT = ControlPort;
		Version="1.0";
		PeerID = PeerId;
		diskSize = disk;
	}
	
	@Override
	public void run(){
		
		Send s = new Send(CONTROLIP,ControlPORT);
		long diskDesoccupied =0;
		
		while(diskDesoccupied >= diskSize){
			
			File file = new File(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator);
			if(file.isDirectory()){	
				if(file.list().length<=0){
					System.out.println("Directory is empty!");	
					return;
				}
			}else{
				System.out.println("This is not a directory");
				return;
			}
			
			File FileToRemove = Tools.lastFileModified(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator);
			diskDesoccupied += FileToRemove.length();
			String name = FileToRemove.getName();
			String FileName[] = name.split("-");
			
			//remover file choiced
			String filepath = System.getProperty("user.dir") + File.separator + "Chunks" + File.separator+name;
			Tools.RemoveFileFromFolder(filepath);
			
			//editar txt
			try {
				Tools.removeLine(FileName[0] + " " + FileName[1]);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String msg = Tools.CreateRemoved(Version, PeerID, FileName[1], FileName[0]);
		
			try {
				s.send(msg.getBytes());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //data in byte[]
	
		}
	}
}
