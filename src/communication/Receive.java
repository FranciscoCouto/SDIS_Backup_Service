package communication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import utilities.Tools;

public class Receive extends Thread{

	private static int PORT;
	private static String ADDR;
	private static int PORTCONTROL;
	private static String ADDRCONTROL;

	public Receive(int servicePort, String multicastAddressStr,String serviceAddressStr, int multicastPort){
		PORT=servicePort;
		ADDR=multicastAddressStr;
		PORTCONTROL=multicastPort;
		ADDRCONTROL=serviceAddressStr;
	}

	
	@Override
	public void run() {
		
		//System.out.println("Listening ip: "+ADDR+" port: "+PORTCONTROL);
		try(MulticastSocket multicastSocket = new MulticastSocket(PORTCONTROL);){
			
		InetAddress group = InetAddress.getByName(ADDR);
		
		multicastSocket.joinGroup(group);
		multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */

		while (true) {
				
			//System.out.println("heyyyy");
			byte[] buf = new byte[67000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			// receive request
			multicastSocket.receive(packet);
			
			try {
				Thread.sleep(Tools.random(0,400));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String[] header = Tools.convertHeader(packet.getData());
			String body = Tools.convertBody(packet.getData());
			
			File dir = new File("C:\\SDIS "+header[2]+"\\Chunks\\");
			
			if (!dir.exists()) {
				   dir.mkdirs();
			}
			
			File file = new File("C:\\SDIS "+header[2]+"\\Chunks\\"+header[4]);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(body);
			bw.close();
			
			//System.out.println("ChunkStored");
			
			//String msgRec = new String(packet.getData(), 0,
			//		packet.getLength());

			//System.out.println("Recebi: " + msgRec);
			
			String msg = Tools.CreateSTORED(Integer.valueOf(header[4]),header[1], header[2], header[3]);
			
			Send s = new Send("225.0.0.3",8888);
			
			s.send(msg.getBytes());
		}
		
		
		}
		
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		//System.out.println("heyyyy");
	}
}
