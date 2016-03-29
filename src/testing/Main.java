package testing;

import java.io.File;
import java.io.IOException;
import communication.Control;
import communication.ReceiveBackup;
import communication.ReceiveRestore;
import protocols.Backup;
import protocols.Delete;
import protocols.Restore;

/**
 * 
 * * > dir /s /B *.java > sources.txt
 *	> javac @sources.txt
 *
 */

public class Main {

	private static String PeerID; //definido pela função
	private static String MCAddress; //endereço multicast
	private static int MCPort;
	private static String FilePath;
	private static int RepDeg;
	private static String protocol;
	
	public static void main(String[] args) throws IOException {

		if (!validArgs(args)) {
			System.exit(0);
		}

		Control control = new Control(15000,"224.224.224.224"); //VALORES DO PEDROO!!!!!2
		control.start();

		switch(protocol.toLowerCase()){
		
		case "backup":
			
			System.out.println("Initializing Backup Channel");

			ReceiveBackup backup = new ReceiveBackup(MCAddress,MCPort,"224.224.224.224",15000); 
																		//JA MUDEI PARA QUE O SEND N FIQUE HARDCODED
			backup.start();												//TA A MANDAR PARA OS DADOS DO MC de CONTROL
			
			Backup back = new Backup(FilePath, RepDeg, MCAddress, MCPort, PeerID, control);
			back.start();
			
			break;			
			
		case "restore":
			
			System.out.println("Initializing Restore Channel");

			ReceiveRestore restore = new ReceiveRestore(MCAddress,MCPort,"224.224.224.224",15000);
																			//JA MUDEI PARA QUE O SEND N FIQUE HARDCODED
			restore.start();												//TA A MANDAR PARA OS DADOS DO MC de CONTROL
			
			Restore rest = new Restore(FilePath, MCAddress, MCPort, PeerID, control);
			rest.start();
			
			break;
			
		case "delete":
			
			System.out.println("Initializing Delete Channel");

			Delete del = new Delete(FilePath, MCAddress, MCPort, PeerID); //AQUI PASSAMOS OS DADOS DO CANAL DE CONTROLO CONFIRMAR
			del.start();
			
			break;
			
		case "reclaim":
			break;
			
		default:
			System.out.println("Unknown Error");
			System.exit(0);
		}	
	
	}

	//java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>  java TestApp 1923 BACKUP test1.pdf 3
	private static boolean validArgs(String[] args) {
		if (args.length != 4) { 
								//ATENÇÃO QUE ISTO PEDE SEMPRE 4 ARGUMENTOS
								//NÃO MUDEI PORQUE NÃO SEI SE ISTO VAI FICAR ASSIM

			System.out.println("Usage:");
			System.out
					.println("\tjava Main <peer_ap> <sub_protocol> <opnd_1> <opnd_2>  ");
			return false;

		} else {
			
			String[] peer_ap = args[0].split(":");
			System.out.println("IP: " + peer_ap[0]);
			MCAddress = peer_ap[0];
			System.out.println("Port: " + peer_ap[1]);
			MCPort = Integer.valueOf(peer_ap[1]);

			if(args[1].toLowerCase().matches("backup|restore|reclaim|delete"))
				protocol = args[1].toLowerCase();
			else{
				System.out.println("Enter a valid protocol!");
				return false;
			}
			
			FilePath = System.getProperty("user.dir") + File.separator + "Files" + File.separator + args[2]; 
			//AQUI COMO PASSAMOS Só O NOME DO FICHEIRO
			//O QUE FIZ FOI ACRESCENTAR O DIRECTORIO ONDE TAMOS A TRABALHAR + PASTA ONDE VAO ESTAR OS FICHEIROS
			//O FILE SEPARATOR É A BARRINHA /
			
			RepDeg = Integer.valueOf(args[3]);
			
			return true;
		}
	}

}