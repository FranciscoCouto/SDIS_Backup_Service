package communication;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import utilities.Tools;

public class ReceiveRestore extends Thread{

	private static String ADDR, CADDR;
	private static int PORT, CPORT;

	/**
	 * Class Constructor
	 * @param address
	 * @param port
	 * @param ControlAdd
	 * @param ControlP
	 */
	public ReceiveRestore(String address, int port, String ControlAdd, int ControlP){
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

			
			if(header[0].toLowerCase().equals("getchunk")){
				
				
				Path path = Paths.get(System.getProperty("user.dir") + File.separator + "Chunks" + File.separator +header[4]+"-"+header[3]+".bak");
				byte[] text = Files.readAllBytes(path);
				
				
				byte[] msg = Tools.CreateCHUNK(Integer.valueOf(header[4]),header[1], header[2],text, header[3]);

				System.out.println("CHUNKNO: "+header[4]);
				
				try {
					Thread.sleep(Tools.random(0,400));
					//Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Send s = new Send(CADDR,CPORT);
				
				s.send(msg);
				
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
