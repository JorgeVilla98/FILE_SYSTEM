package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import cliente.OyenteServidor;
import mensaje.*;
import utils.*;

public class OyenteCliente extends Thread {

	private ObjectOutputStream fout; // flujo salida hacia cliente
	private ObjectInputStream fin; // flujo entrada a servidor
	private Servidor servidor; // servidor al que pertenece el listener

	public OyenteCliente(Socket s, Servidor servidor) {
		try {
			this.fout = new ObjectOutputStream(s.getOutputStream());
			this.fin = new ObjectInputStream(s.getInputStream());
			this.servidor = servidor;
		} catch (IOException e) {
			System.out.println("Problema al inicializar el Oyente del Cliente");
			e.printStackTrace();
		}

	}

	public void run() {

		Mensaje m = null;
		while (true) {
			try {
				m = (Mensaje) fin.readObject(); // recibimos mensaje por el flujo de entrada
				switch (m.getTipo()) {
					case CONEXION_CLIENTE: { // recibimos un msg del cliente pidiendo conexion con el servidor
						conexionCliente((MSG_CONEXION_CLIENTE) m); // intentamos la conexion con el cliente
						break;
					}
					case LISTA_USUARIOS: { // nos llega un msg pidiendo información de los usuarios
						infoSistema(m);
						break;
					}
					case NUEVO_FICHERO: { // llega un msg para añadir un fichero por parte del cliente
						MSG_NUEVO_FICHERO mnf = (MSG_NUEVO_FICHERO) m;
						System.out.println("Solicitud de añadir fichero " + mnf.getNombreFichero() );
						System.out.println("Cliente: " + mnf.getIdUsuario() + " IP: " + mnf.getIpOrigen() + "\n");
						// añadimos el nuevo fichero
						nuevoFichero(mnf);
						break;
					}
					case CERRAR_CONEXION: { // nos llega un msg del cliente para cerrar la conexion
						MSG_CERRAR_CONEXION mcc = (MSG_CERRAR_CONEXION) m;
						System.out.println("Solicitud de cierre de conexión recibida del cliente: "
								+ mcc.getIdUsuario() + " en IP: " + mcc.getIpOrigen() + "\n");
						cerrarConexion(mcc);
						return;
					}
					case PEDIR_FICHERO: { // nos llega un mensaje del cliente pidiendonos un fichero
						MSG_PEDIR_FICHERO mpf = (MSG_PEDIR_FICHERO) m;
						System.out.println("El cliente : "  + mpf.getIdUsuario() + "(" + mpf.getIpOrigen() + ") quiere " +
										   "descargar el fichero con nombre " + mpf.getNombreFichero());
						avisoEmisor(mpf);
						break;
					}
					case EMISOR_LISTO: { // nos llega un mensaje diciendo que el emisor esta preparado para enviar
						// el fichero mediante P2P
						MSG_EMISOR_LISTO mel = (MSG_EMISOR_LISTO) m;
						System.out.println("Notificación de que el emisor está listo para enviar el archivo. Emisor: "
								+ mel.getIdUsuario() + " en IP: " + mel.getIpOrigen() + "\n");
						System.out.println("Enviando mensaje a receptor " + mel.getIdPeticion());
						avisoReceptor(mel);
						break;
					}
					case DESCARGA_EXITO:
						MSG_DESCARGA_EXITO mde = (MSG_DESCARGA_EXITO) m;
						System.out.println("El usuario " + mde.getIdUsuario() + " ha descargado el fichero con exito");
						System.out.println("El fichero descargado se ha guardado con el nombre " + mde.getNombreFichero()
								+ " en la ruta " + mde.getRuta());
						System.out.println("A continuacion se guardara la informacion del fichero en el sistema...\n");
						servidor.nuevoFichero(mde.getNombreFichero(), mde.getRuta(), mde.getIdUsuario());
						System.out.println("Se ha guardado la informacion sobre el nuevo fichero\n");
						break;
					case ELIMINAR_FICHERO:
						MSG_ELIMINAR_FICHERO mef = (MSG_ELIMINAR_FICHERO) m;
						System.out.println("El usuario " + mef.getIdUsuario() + " va a eliminar el fichero " + mef.getNombreFichero());
						boolean ok;
						if(servidor.eliminarFichero(mef.getIdUsuario(), mef.getNombreFichero())){
							System.out.println("Se ha eliminado el fichero con exito");
							ok = true;
						}
						else{
							System.out.println("No se ha encontrado el fichero " + mef.getNombreFichero() + " en la lista de ficehros de " + mef.getIdUsuario());
							ok = false;
						}
						fout.writeObject(new MSG_ELIMINAR_FICHERO_OK(servidor.getServerIp(), mef.getIpOrigen(),mef.getNombreFichero(), ok));
						break;
					default:
						System.out.println("Ocurrió un error al escuchar un mensaje...");
				}

			} catch (Exception e) {
				System.out.println("Problema al ejecutar el oyente del cliente, cerrando la conexión y eliminando datos \n");
				assert m != null;
				servidor.delUsuario(m.getIdUsuario()); // borramos el usuario del monitor de usuarios
				servidor.delFlujoUsuario(m.getIdUsuario()); // borramos el flujo del usuarion del monitor de fujos de
				// usuario
				e.printStackTrace();
				return;
			}
		}

	}

	private void nuevoFichero(MSG_NUEVO_FICHERO msg) throws IOException {
		if (servidor.nuevoFichero(msg.getNombreFichero(), msg.getRuta(), msg.getIdUsuario()))
			// usamos al servidor para añadir el fichero
			fout.writeObject(new MSG_NUEVO_FICHERO_OK(servidor.getServerIp(), msg.getIpOrigen(),msg.getNombreFichero())); // si la carga ha sido exitosa, notificamos al cliente
		else{
			fout.writeObject(new MSG_NUEVO_FICHERO_ERROR(servidor.getServerIp(), msg.getIpOrigen(),msg.getNombreFichero()));
		}
	}

	private void conexionCliente(MSG_CONEXION_CLIENTE msg) throws IOException {
		boolean ok;
		if (servidor.usuarioExiste(msg.getIdUsuario())) { // si el usuario ya existe mandamos un mensaje al cliente
			// notificando de un error
			System.out.println("\nEl usuario " + msg.getIdUsuario() + " ha intentado conectarse pero ya hay otro usuario con el mismo nombre");
			fout.writeObject(new MSG_CONEXION_ERROR(servidor.getServerIp(), msg.getIpOrigen(), msg.getIdUsuario()));
		} else {
			//creamos un nuevo usuario
			Usuario u = new Usuario(msg.getIdUsuario(), msg.getIpOrigen()); // creamos un usuario con id y la ip del
			//se crea un nuevo flujo de usuario con los fin y fout actuales
			FlujoUsuario uf = new FlujoUsuario(msg.getIdUsuario(), fout, fin); // creamos el flujo de usuario
			servidor.nuevoUsuario(u); // añadinos el usuario al servidor
			servidor.nuevoFlujoUsuario(uf); // añadimos el flujo al servidor
			System.out.println("\nEl usuario " + msg.getIdUsuario() + " se ha conectado y se ha añadido su informacion al sistema");
			// enviamos mensaje confirmacion
			fout.writeObject(new MSG_CONEXION_CLIENTE_OK(msg.getIpDestino(), msg.getIpOrigen()));
		}

	}

	private void cerrarConexion(MSG_CERRAR_CONEXION msg) throws IOException {
		System.out.println("Cerrando conexión con " + msg.getIdUsuario());
		fout.writeObject(new MSG_CERRAR_CONEXION_OK(msg.getIpDestino(), msg.getIpOrigen(), msg.getIdUsuario())); // mandamos
		servidor.delUsuario(msg.getIdUsuario()); // eliminamos el usuario del servidor
		servidor.delFlujoUsuario(msg.getIdUsuario()); // eliminamos el flujo de usuario del servidor
		fout.close(); // cerramos el flujo de salida del servidor con el usuario

	}

	//Buscamos al usuario propietario del fichero, si es que existe
	private void avisoEmisor(MSG_PEDIR_FICHERO msg) throws IOException {
		Usuario emisor = servidor.getOwnerFichero(msg.getNombreFichero());
		if(emisor != null && !Objects.equals(emisor.getIdUsuario(), msg.getIdUsuario())){
			System.out.println("El propietario del fichero " + msg.getNombreFichero() + " es el usuario " + emisor.getIdUsuario() +"(" + emisor.getIpUsuario() + ")");
			String ruta = servidor.getRutaFichero(emisor.getIdUsuario(), msg.getNombreFichero());
			//Se obtiene el flujo de salida para el usuario propietario, para poder mandarle un mensaje avisandole de que
			//tiene que enviar el fichero solicitado al usuario.
			ObjectOutputStream foutEmisor = servidor.getFoutUsuario(emisor.getIdUsuario());
			foutEmisor.writeObject(new MSG_AVISO_EMISOR(servidor.getServerIp(),emisor.getIpUsuario(),msg.getIdUsuario(),msg.getNombreFichero(),ruta));
			foutEmisor.flush();
			System.out.println("Se ha mandado un mensaje al usuario con ip " + emisor.getIpUsuario());
		}
		else{
			boolean propioUser = false;
			//Si el propio usuario que solicita es el dueño del fichero,
            if(emisor != null && Objects.equals(emisor.getIdUsuario(), msg.getIdUsuario())){
				System.out.println("El propio usuario tiene el fichero que esta solicitando, no es necesario descargarlo");
				propioUser = true;
			}
			//No se ha encontrado a un usuario con dicho fichero
			else{System.out.println("No se ha encontrado ningun usuario que posea el fichero " + msg.getNombreFichero());}
			//Se manda un mensaje al usuario avisando de que ha ocurrido un fichero buscando el fichero, ya sea porque el usuario es el propietario
			//de dicho fichero o porque no se ha encontrado
			fout.writeObject(new MSG_PEDIR_FICHERO_ERROR(msg.getIpDestino(), msg.getIpOrigen(), msg.getNombreFichero(), msg.getNombreFichero(), propioUser));
			fout.flush();
		}
	}

	// notificamos al receptor de que el emisor y esta listo y se puede conectar a este para
	// descargar el fichero solicitado
	private void avisoReceptor(MSG_EMISOR_LISTO msg) throws IOException {
		ObjectOutputStream foutReceptor = servidor.getFoutUsuario(msg.getIdPeticion());
		String ipReceptor = servidor.getIpUsuario(msg.getIdPeticion());
		foutReceptor.writeObject(new MSG_AVISO_RECEPTOR(msg.getIpOrigen(), ipReceptor, msg.getPuerto(), msg.getNombreFichero()));

	}

	//se recibe una solicitud de informacion por parte de un cliente, en la cual se enviara la informacion del sistema
	//a este y tambien se printeara en el propio servidor, para poder facilitar la depuracion del programa en el futuro
	private void infoSistema(Mensaje m) throws IOException {
		MSG_LISTA_USUARIOS msgListaUsuarios = (MSG_LISTA_USUARIOS) m;
		System.out.println("Solicitud de información de usuario recibida del cliente: "
				+ msgListaUsuarios.getIdUsuario() + " en IP: " + msgListaUsuarios.getIpOrigen());
		System.out.println("El cliente solicita información de usuario");

		// Obtenemos la lista de usuarios del servidor
		ArrayList<Usuario> listaUsuarios = servidor.getListaUsuarios();

		// Mostramos la información de cada usuario, asi como los ficheros que cada uno dispone
		System.out.println("Información de usuarios:");
		for (Usuario usuario : listaUsuarios) {
			System.out.println("ID: " + usuario.getIdUsuario() + ", IP: " + usuario.getIpUsuario());
			System.out.println("Archivos:");
			for (FileInfo fileInfo : usuario.getFicheros()) {
				System.out.println("- Nombre: " + fileInfo.getNombreFichero() + ", Ruta: " + fileInfo.getRuta());
			}
		}
		// Escribimos la info por el flujo de salida y mandamos el mensaje al usuario que solicito dicha informacion
		MSG_LISTA_USUARIOS_OK mluo = new MSG_LISTA_USUARIOS_OK(m.getIpDestino(), m.getIpOrigen(), listaUsuarios);
		fout.writeObject(mluo);
		fout.flush();
	}
}
