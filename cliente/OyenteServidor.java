package cliente;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import mensaje.*;
import utils.*;

public class OyenteServidor extends Thread {
	private Socket sock;//Socket perteneciente al cliente
	private ObjectInputStream fin;//Flujo de entrada para recibir mensajes del servidor
	private Cliente cliente; //Cliente que gestiona el oyente del servidor
	public OyenteServidor(Socket s, Cliente cliente) {
		try {
			this.cliente = cliente;
			this.sock = s;
			this.fin = new ObjectInputStream(sock.getInputStream());//Se obtiene el mismo flujo de entrada del cliente

		} catch (IOException e) {
			System.out.println("Ha ocurrido un problema inciando el oyente del servidor");
			e.printStackTrace();
		}

	}

	public void run() {

		try {
			while (true) {
				Mensaje m = (Mensaje) fin.readObject(); //Se lee un mensaje proveniente del servidor (oyente cliente)
				switch (m.getTipo()) {
					//Confirmacion de conexion, se libera el semaforo para que el cliente pueda acceder al menu
					case CONEXION_OK:
						System.out.println("Conexion realizada con server");
						cliente.getSemConexion().release();
						break;
					//Ha ocurrido un error inciando la conexion, se vuelve a intentar dicha conexion
					case CONEXION_ERROR:
						reintentarConexion((MSG_CONEXION_ERROR) m);
						break;
					//Se recibe una confirmacion del servidor con la informacion del sistema
					case LISTA_USUARIOS_OK:
						System.out.println("Informacion del sistema recibida \n Usuarios conectados: ");
						printInfoSistema(((MSG_LISTA_USUARIOS_OK) m).getListaUsuarios());
						cliente.getSemMenu().release();
						break;
					//Se recibe un mensaje indicando que el fichero nuevo ha sido añadido con exito
					case NUEVO_FICHERO_OK:
						MSG_NUEVO_FICHERO_OK mnfo = (MSG_NUEVO_FICHERO_OK) m;
						System.out.println("Fichero " + mnfo.getNombreFichero() + " añadido al sistema con exito");
						cliente.getSemMenu().release();
						break;
					//Se recibe un mensaje indicando que ha ocurrido un error añadiendo el nuevo ficehro al sistema
					case NUEVO_FICHERO_ERROR:
						MSG_NUEVO_FICHERO_ERROR mnfe = (MSG_NUEVO_FICHERO_ERROR) m;
						System.out.println("No se ha podido añadir el fichero al sistema");
						cliente.getSemMenu().release();
						break;
					//Se recibe un mensaje indicando al cliente que debe funcionar como emisor, creando
					//un server socket para que el receptor se conecte y reciba el fichero solicitado
					case AVISO_EMISOR:
						enviarFichero((MSG_AVISO_EMISOR) m);
						cliente.getSemMenu().release();
						break;
					//Se recibe un mensaje avisando al usuario que ha solicitado el fichero que ya puede descargarlo,
					//ya que el emisor ha abierto una conexion para que se pueda descargar el fichero
					case AVISO_RECEPTOR:
						descargarFichero((MSG_AVISO_RECEPTOR) m);
						cliente.getSemMenu().release();
						break;
					//Se confirma el cierre de conexion, todos los flujos pertinentes han sido cerrados en el oyente del cliente
					case CERRAR_CONEXION_OK:
						System.out.println("Bye " + m.getIdUsuario());
						cliente.getSemMenu().release();
						fin.close();
						sock.close();
						return;
					//Ha ocurrido un error pidiendo un fichero para descargar, ya sea porque el fichero solicitado ya se dispone,
					//o porque no se ha encontrado
					case PEDIR_FICHERO_ERROR:
						if (((MSG_PEDIR_FICHERO_ERROR) m).getPropioUser()) {
							System.out.println("Ya se dispone de el archivo solicitado: " + ((MSG_PEDIR_FICHERO_ERROR) m).getNombreFichero());
						} else
							System.out.println("No se ha encontrado el fichero " + ((MSG_PEDIR_FICHERO_ERROR) m).getNombreFichero());
						cliente.getSemMenu().release();
						break;
					//Comprobamos si el fichero se ha eliminado correctamente
					case ELIMINAR_FICHERO_OK:
						MSG_ELIMINAR_FICHERO_OK mefo = (MSG_ELIMINAR_FICHERO_OK) m;
						if((mefo.isOk())) {
							System.out.println("Se ha eliminado el fichero existosamente");
						}
						else{System.out.println("No se ha encontrado el fichero solicitado");}
						cliente.getSemMenu().release();
						break;
					default:
						System.out.println("Mensaje no valido");
						break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (sock != null) sock.close();
				if (fin != null) fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	//Se reintenta la conexion, introduciendo un nuevo id de usuario que no exista en la base de datos del servidor
	//y se vuelve a mandar un mensaje para intentar la conexion con el servidor
	private void reintentarConexion(MSG_CONEXION_ERROR msg) {
		try {
			cliente.reintentarConexion();
			cliente.enviarMensaje(new MSG_CONEXION_CLIENTE(cliente.getIpCliente(), msg.getIpOrigen(), cliente.getUserId()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Se crea un socket nuevo para que el receptor envie el fichero al usuario que lo solicita
	private void enviarFichero(MSG_AVISO_EMISOR mae) {
		System.out.println("El usuario " + mae.getIdPeticion() + " esta pidiendo el fichero " + mae.getNombreFichero());
		String rutaAbsoluta = mae.getRuta() + File.separator + mae.getNombreFichero();
		new Emisor(rutaAbsoluta, cliente, mae).start();
	}

	private void descargarFichero(MSG_AVISO_RECEPTOR msg) {
        String ip = msg.getIpConexion();
        int port = msg.getPuertoConexion();
        System.out.println("Conexion P2P lista, a continuación se realizará la conexión a " + ip + ":" + port);
        String ruta = cliente.getRutaDescarga();
        new Receptor(ip, port, msg.getNombreFichero(), ruta, cliente).start();
	}

	private void printInfoSistema(ArrayList<Usuario> listaUsuarios) {
		System.out.println("\nLista de usuarios");
		for (Usuario usuario : listaUsuarios) {
			System.out.println("ID: " + usuario.getIdUsuario() + ", IP: " + usuario.getIpUsuario());
			System.out.println("Archivos:");
			for (FileInfo fileInfo : usuario.getFicheros()) {
				System.out.println("- Nombre: " + fileInfo.getNombreFichero() + ", Ruta: " + fileInfo.getRuta());
			}
		}
	}

}
