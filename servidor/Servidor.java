package servidor;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;

import utils.*;

public class Servidor extends Thread {
	
	
	private final int puerto; //puerto del servidor
	private final String ip; //ip del servidor
	private Monitor monitor; //monitor de la informacion de los usarios
	
	public Servidor(int puerto, String ip, Monitor monitor) {
		this.puerto = puerto;
		this.ip = ip;
		this.monitor = monitor;
	}
	public void run(){
		ServerSocket s;
		try {
			s = new ServerSocket(this.puerto); //Creamos un socket con el puerto del servidor
			while(true) {
				System.out.println("Esperando conexiones...");
				new OyenteCliente(s.accept(),this).start(); // detenemos al servidor hasta que llega un cliente
				System.out.println("Una nueva conexion ha sido establecida");
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getServerIp() {
		return this.ip;
	}
	public boolean usuarioExiste(String user_id) {
		return monitor.usuarioExiste(user_id);
	}
	public void nuevoUsuario(Usuario u) {
		monitor.nuevoUsuario(u);
	}
	public  boolean nuevoFichero(String filename, String path, String user_id) {
		return monitor.nuevoFichero(filename,path,user_id);
	}
	public boolean eliminarFichero(String user_id, String nombre_fichero){
		return monitor.eliminarFichero(user_id, nombre_fichero);
	}
	public void nuevoFlujoUsuario(FlujoUsuario uf) {
		monitor.nuevoFlujoUsuario(uf);
	}
	public ArrayList<Usuario> getListaUsuarios(){
		return monitor.getListaUsuarios();
	}
	public void delUsuario(String user_id) {
		monitor.delUsuario(user_id);
	}
	public void delFlujoUsuario(String user_id) {
		monitor.delFlujoUsuario(user_id);
	}
	public Usuario getOwnerFichero(String filename) {
		return monitor.getOwnerFichero(filename);
	}
	public ObjectOutputStream getFoutUsuario(String id_usuario) {
		return monitor.getFoutUsuario(id_usuario);
	}

	public String getRutaFichero(String nombre_usuario, String nombre_fichero){
        return monitor.getRutaFichero(nombre_usuario, nombre_fichero);
	}

	public String getIpUsuario(String nombre){
		return monitor.getIpUsuario(nombre);
	}


}
