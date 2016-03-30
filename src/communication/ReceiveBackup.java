package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import utilities.Tools;

public class ReceiveBackup extends Thread{

	private static String ADDR, CADDR;
	private static int PORT, CPORT;

	public ReceiveBackup(String address, int port, String ControlAdd, int ControlP){
		ADDR=address;
		PORT=port;
		CPORT = ControlP;
		CADDR = ControlAdd;
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
			
			String[] header = Tools.convertHeader(packet.getData());

			System.out.println(packet.getData());
			if(header[0].toLowerCase().equals("putchunk")){
				byte[] body = Tools.convertBody2(packet.getData());
				

				body = Tools.trim(body,0);
				System.out.println("STORED: " + body.length + " BYTES");

				Tools.SaveChunks(header[4], header[3], body);				
			
				String msg = Tools.CreateSTORED(Integer.valueOf(header[4]),header[1], header[2], header[3]);
				
				try {
					Thread.sleep(Tools.random(0,400));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				Send s = new Send(CADDR,CPORT);
				
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
