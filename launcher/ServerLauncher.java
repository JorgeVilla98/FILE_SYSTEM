package launcher;

import servidor.*;

public class ServerLauncher {
	public static void main(String[] args) {
	
		if(args.length!=2) {
			System.out.println("Argumentos: ip_server server_port");
			return;
		}
		
		String ip = args[0];
		int port = Integer.parseInt(args[1]);

		Monitor monitor = new Monitor();

		//Iniciamos el server
		Servidor servidor = new Servidor(port, ip, monitor);
		servidor.start();
		try {
			servidor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
	}
}
