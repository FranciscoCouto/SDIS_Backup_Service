package communication;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import utilities.Tools;

public class Receive extends Thread{

	private static String ADDR;
	private static int PORT;

	public Receive(int servicePort, String multicastAddressStr,String serviceAddressStr, int multicastPort){
		ADDR=multicastAddressStr;
		PORT=multicastPort;
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(PORT);){
			
		InetAddress group = InetAddress.getByName(ADDR);
		
		multicastSocket.joinGroup(group);
		multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */

		while (true) {

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
			
			if(header[0].toLowerCase().equals("putchunk")){
				String body = Tools.convertBody(packet.getData()).trim();
	
				System.out.println("STORED: " + body.trim().getBytes().length + " BYTES");
				
				
				Tools.SaveChunks(header[4], header[3], body);				
			
				String msg = Tools.CreateSTORED(Integer.valueOf(header[4]),header[1], header[2], header[3]);
				
				Send s = new Send("225.0.0.3",8888);
				
				s.send(msg.getBytes());
			}
			
			else if(header[0].toLowerCase().equals("getchunk")){
				
				Scanner sc = new Scanner(new File("C:\\SDIS\\Chunks\\"+header[4]+"-"+header[3]));
				List<String> lines = new ArrayList<String>();
				while (sc.hasNextLine()) {
				  lines.add(sc.nextLine());
				}

				String text = lines.toString();
				
				String msg = Tools.CreateCHUNK(Integer.valueOf(header[4]),header[1], header[2],text, header[3]);
				
				Send s = new Send("225.0.0.3",8888);
				
				s.send(msg.getBytes());
				
			}
			
			else{
				System.out.println("INVALID TYPE OF MESSAGE RECEIVED!");
				return;
			}
		}
		
		
		}
		
		catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
