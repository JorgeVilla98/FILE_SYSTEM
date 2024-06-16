package cliente;

import java.io.*;
//import java.io.File;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;//

//import java.net.SocketAddress;
//import java.net.InetSocketAddress;


import mensaje.*;

public class Cliente extends Thread{

	private final String ip_cliente;
	private final String ip_servidor;
	private final int puerto;
	private String user_id; //id del usuario que se conecta con el cliente
	private ObjectOutputStream fout;
	private String rutaDescarga = null;
	private Semaphore semConexion = new Semaphore(0); //Creamos un semaforo de espera hasta que el ClientListener
	private Semaphore semMenu = new Semaphore(1);													   //acepte la conexion
	
	//private ServerListener SL;
	
	public Cliente(int puerto, String ip_cliente, String ip_servidor) {
		this.puerto = puerto;
		this.ip_cliente = ip_cliente;
		this.ip_servidor = ip_servidor;
	}
	
	public Semaphore getSemConexion() {
		return this.semConexion;
	}
	public Semaphore getSemMenu(){return this.semMenu;}
	public String getIpCliente() {
		return this.ip_cliente;
	}
	public String getUserId() {
		return this.user_id;
	}

	public void run() {
	
		try {
			//Se crea un socket con la ip del servidor, asi como el puerto de conexion
			Socket socket = new Socket(ip_servidor, puerto);
			this.fout = new ObjectOutputStream(socket.getOutputStream());

			//Se introduce el nombre del usuario
			Scanner scan = new Scanner(System.in);
			System.out.println("Introduce el nombre del usuario: ");
			this.user_id = scan.nextLine();
			while(this.user_id == null || this.user_id.isEmpty()) {
				System.out.println("Debe introducir un nombre de usuario valido.");
				this.user_id = scan.nextLine();
			}

			//Se manda un mensaje de conexion al servidor
			new OyenteServidor(socket,this).start();
			fout.writeObject(new MSG_CONEXION_CLIENTE(ip_cliente, ip_servidor,user_id));

			//El semaforo bloquea el acceso al menu hasta que se confirme la conexion
			semConexion.acquire();

			System.out.println("Bienvenido, "+this.user_id);
			
			int option;
			do{
				//Se bloquea el menu hasta que no se reciba un mensaje del servidor, en cuyo caso se hace un release()
				semMenu.acquire();
				menu();
				option = scan.nextInt();
				Mensaje m = null;
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					switch(option) {
						case 1:
							m = new MSG_LISTA_USUARIOS(ip_cliente, ip_servidor, user_id);
							break;
						case 2:
							System.out.println("Introduce el nombre del fichero que quieres descargar:");
							String descarga = br.readLine();
							rutaDescarga = null;
							System.out.println("Introduce la ruta donde quieres almacenar el fichero: ");
							rutaDescarga = br.readLine();
							m = new MSG_PEDIR_FICHERO(ip_cliente, ip_servidor,user_id,descarga);
							break;
						case 3:
							boolean ficheroCreado = false;
							do {
								System.out.println("Introduce nombre de fichero: ");//Pedimos el nombre del fichero que vamos a añadir
								String nombre_fichero_nuevo, ruta;
								nombre_fichero_nuevo = br.readLine();

								System.out.println("Introduce ruta de fichero: ");
								ruta = br.readLine();
								File file = new File(ruta + File.separator + nombre_fichero_nuevo); // Usar File.separator para garantizar portabilidad en la ruta
								try {
									if (file.createNewFile()) {
										System.out.println("\nFichero creado correctamente.\n");
										m = new MSG_NUEVO_FICHERO(ip_cliente, ip_servidor, user_id, nombre_fichero_nuevo, ruta);
										ficheroCreado = true;
									} else {
										System.out.println("No se pudo crear el fichero. Ya existe un fichero con el mismo nombre en la ruta especificada.");
										System.out.println("¿Desea intentarlo de nuevo? (s/n)");
										String respuesta = scan.next();
										if (!respuesta.equalsIgnoreCase("s")) {
											break; // Salir del bucle si el usuario no desea intentarlo de nuevo
										}
									}
								} catch (IOException e) {
									System.out.println("Error al crear el fichero.");
									e.printStackTrace();
								}
							} while (!ficheroCreado);
							break;
						case 4:
							System.out.println("Introduzca el nombre del fichero que quiere eliminar: ");
							String nombre_fichero_del = br.readLine();
							while(nombre_fichero_del == null || nombre_fichero_del.isEmpty()){
								System.out.println("Por favor, introduzca un nombre ");
								nombre_fichero_del = br.readLine();
							}
							m = new MSG_ELIMINAR_FICHERO(ip_cliente, ip_servidor, user_id, nombre_fichero_del);
							break;
						case 0:
							m = new MSG_CERRAR_CONEXION(ip_cliente, ip_servidor,user_id);
							break;
						default: System.out.println("Opcion no valida");
					}

				}catch(Exception e) {
					e.printStackTrace();
				}
				if(m!=null) {
					fout.writeObject(m);
				}
			}while(option!=0);
			scan.close();
		
		}catch(Exception e) {
			System.out.println("Ha ocurrido un erro en el cliente...\n");
			e.printStackTrace();
			return;
		
		}	
		
	}
	public void enviarMensaje(Mensaje m) {
		try {
			fout.writeObject(m);
		} catch (IOException e) {
			System.out.println("Impossible to send the message");
			e.printStackTrace();
		}
	}
	private void menu() { //interfaz de usuario
		System.out.println("\nMENU CLIENTE\n");

		System.out.println("1 -Mostrar informacion del sistema");
		System.out.println("2 -Descargar fichero");
		System.out.println("3 -Añadir fichero");
		System.out.println("4 -Eliminar fichero");
		System.out.println("0 -Salir");
		System.out.print("\nIntroduzca una opcion: ");
	}
	
	public void reintentarConexion() throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Ya hay un usuario con este nombre");
		System.out.print("Introduzca un usuario valido: ");
        this.user_id = scan.nextLine();
	}

	public void notificarDescargaFichero(String ruta, String nombreFichero) {
		MSG_DESCARGA_EXITO mde = new MSG_DESCARGA_EXITO(ip_cliente, ip_servidor, user_id, nombreFichero, ruta);
		enviarMensaje(mde);
	}

	public String getRutaDescarga(){return rutaDescarga;}
}
