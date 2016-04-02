package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import utilities.Tools;


public class ReceiveBackup extends Thread{

	private static String ADDR, CADDR, PeerID;
	private static int PORT, CPORT;
	
	/**
	 * Class Constructor
	 * @param address
	 * @param port
	 * @param ControlAdd
	 * @param ControlP
	 * @param PeerId
	 * @param c
	 */
	public ReceiveBackup(String address, int port, String ControlAdd, int ControlP, String PeerId){
		ADDR = address;
		PORT = port;
		CPORT = ControlP;
		CADDR = ControlAdd;
		PeerID = PeerId;
	}

	
	@Override
	public void run() {

		try(MulticastSocket multicastSocket = new MulticastSocket(PORT);){
			
		InetAddress group = InetAddress.getByName(ADDR);
		
		multicastSocket.joinGroup(group);
		//multicastSocket.setLoopbackMode(true); /** setting whether multicast data will be looped back to the local socket */

		while (true) {

			byte[] buf = new byte[67000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			// receive request
			multicastSocket.receive(packet);
			
			String[] header = Tools.convertHeader(packet.getData());

			System.out.println(packet.getData());
			
			if(header[0].toLowerCase().equals("putchunk") && !header[2].equals(PeerID) && ((Tools.returnDiskSize(PeerID)-packet.getLength()) > 0)){
				
				int garbage = Tools.convertBody(packet.getData());
				byte[] body = Tools.trim(packet.getData(),garbage);
				
				System.out.println("STORED: " + body.length + " BYTES");

				Tools.SaveChunks(header[4], header[3], body);
				
				Tools.ChangeDiskSize("backup", body.length, PeerID);
			
				String msg = Tools.CreateSTORED(Integer.valueOf(header[4]),header[1], PeerID , header[3]);
				
				
				try {
					Thread.sleep(Tools.random(0,400));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				Send s = new Send(CADDR,CPORT);
				
				s.send(msg.getBytes());
				
				Tools.saveRep(header[5].trim(), header[3]);
			
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
