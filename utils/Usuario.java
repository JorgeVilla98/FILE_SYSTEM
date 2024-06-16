package utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;
	private final String id;
	private final String ip;
	private ArrayList<FileInfo> infoFicheros;

	public Usuario(String id, String ip) {
		this.id = id;
		this.ip = ip;
		this.infoFicheros =new ArrayList<FileInfo>();
	}
	public String getIdUsuario() {
		return this.id;
	}
	public String getIpUsuario() {
		return this.ip;
	}

	public ArrayList<FileInfo> getFicheros() {
		return new ArrayList<>(this.infoFicheros);

	}
	public void nuevoFichero(String filename, String path) {
		this.infoFicheros.add(new FileInfo(filename, path));
	}
	public boolean eliminarFichero(String filename) {
		for (FileInfo fileInfo : infoFicheros) {
			if (fileInfo.getNombreFichero().equals(filename)) {
				infoFicheros.remove(fileInfo);
				return true;
			}
		}
		return false;
	}
	public boolean tieneFichero(String filename, String ruta_filename) {
		for (FileInfo fileInfo : infoFicheros) {
			if (fileInfo.getNombreFichero().equals(filename) && fileInfo.getRuta().equals(ruta_filename)) {
				return true; // File with the same filename and ruta_filename already exists
			}
		}
		return false; // File with the same filename and ruta_filename does not exist
	}
}
