package protocols;

import java.io.File;
import java.io.IOException;

import utilities.Tools;
import communication.Send;

public class Reclaiming extends Thread{
	
	static String CONTROLIP, Version,PeerID;
	static int  ControlPORT;
	
	/**
	 * Class Constructor
	 * @param ControlIP
	 * @param ControlPort
	 * @param PeerId
	 */
	public Reclaiming(String ControlIP, int ControlPort, String PeerId){
		
		CONTROLIP = ControlIP;
		ControlPORT = ControlPort;
		Version="1.0";
		PeerID = PeerId;
	}
	
	@Override
	public void run(){
		
		int counter=0;
		Send s = new Send(CONTROLIP,ControlPORT);

		File FileToRemove = Tools.lastFileModified(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator);
		
		while(counter < 1){
			
			String name = FileToRemove.getName();
			String FileName[] = name.split("-");
			
			//remover file choiced
			String filepath = System.getProperty("user.dir") + File.separator + "Chunks" + File.separator+name;
			Tools.RemoveFileFromFolder(filepath);
			
			String msg = Tools.CreateRemoved(Version, PeerID, FileName[0], FileName[1]);
		
			try {
				s.send(msg.getBytes());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //data in byte[]

			counter++;		
		}
	}
}
