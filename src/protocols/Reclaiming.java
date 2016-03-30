package protocols;

import java.io.IOException;

import communication.Send;
import utilities.Tools;

public class Reclaiming extends Thread{
	
	static String CONTROLIP, Version,PeerID;
	static int  ControlPORT;
	private static String FILE;
	
	public Reclaiming(String File, String ControlIP, int ControlPort, String PeerId){
		
		CONTROLIP = ControlIP;
		ControlPORT = ControlPort;
		FILE = File;
		Version="1.0";
		PeerID = PeerId;
		
	}
	
	@Override
	public void run(){
		
		int counter=0;
		
		while(counter < 1){
			
			//Necessaria função para ir buscar o fileid e o chunkNo do ficheiro que se vai remover
			
			//String msg = Tools.CreateRemoved(Version, PeerID, fileID, chunkNo);
			
			Send s = new Send("225.0.0.3",ControlPORT);
			
			/*try {
				s.send(msg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			counter++;
				
		}
	
	}
}
