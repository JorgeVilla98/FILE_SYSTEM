

//Launcher del cliente: args[].> ip del servidor, puerto, ip del cliente

package launcher;

import cliente.Cliente;

public class ClientLauncher {

	public static void main(String[] args) {
		if(args.length!=3) {
			System.out.println("Usage: args[0]->server ip, args[1]->port server, args[2]-> client ip");
			System.out.println("Ejemplo: localhost 5550 192.169.0.21");
			return;
		}
		String ip_servidor = args[0];//server ip
		int puerto=Integer.parseInt(args[1]);
		String ip_cliente = args[2];//client ip
		Cliente cliente = new Cliente(puerto, ip_cliente, ip_servidor);
		cliente.start();
		try {
			cliente.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
