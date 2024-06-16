package servidor;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import utils.FileInfo;
import utils.Usuario;
import utils.FlujoUsuario;

public class Monitor {

	private final Map<String, Usuario> usuarios = new HashMap<>();
	private final ArrayList<FlujoUsuario> flujos = new ArrayList<>();
	private final Lock monitorlock = new ReentrantLock();

	public Monitor() {
	}

	public boolean usuarioExiste(String user_id) {
		monitorlock.lock();
		try {
			return usuarios.containsKey(user_id);
		} finally {
			monitorlock.unlock();
		}
	}

	public Usuario getOwnerFichero(String filename) {
		monitorlock.lock();
		try {
			for (Usuario usuario : usuarios.values()) {
				for (FileInfo f : usuario.getFicheros()) {
					if (f.getNombreFichero().equals(filename)) {
						return usuario;
					}
				}
			}
			return null;
		} finally {
			monitorlock.unlock();
		}
	}

	public void nuevoUsuario(Usuario u) {
		monitorlock.lock();
		try {
			usuarios.put(u.getIdUsuario(), u);
		} finally {
			monitorlock.unlock();
		}
	}
	public boolean eliminarFichero(String user_id, String nombre_fichero){
		monitorlock.lock();
		try{
			return usuarios.get(user_id).eliminarFichero(nombre_fichero);
		} finally {
			monitorlock.unlock();
		}
	}
	public boolean nuevoFichero(String filename, String ruta_filename, String user_id) {
		monitorlock.lock();
		try {
			Usuario usuario = usuarios.get(user_id);
			if (usuario != null) {
				// Verificar si el usuario ya tiene un archivo con el mismo nombre y ruta
				if (!usuario.tieneFichero(filename, ruta_filename)) {
					usuario.nuevoFichero(filename, ruta_filename);
					return true; // Archivo añadido con éxito
				} else {
					return false; // El archivo ya existe
				}
			}
			return false; // Usuario no encontrado
		} finally {
			monitorlock.unlock();
		}
	}

	public ArrayList<Usuario> getListaUsuarios() {
		monitorlock.lock();
		try {
			return new ArrayList<>(usuarios.values());
		} finally {
			monitorlock.unlock();
		}
	}

	public void delUsuario(String user_id) {
		monitorlock.lock();
		try {
			usuarios.remove(user_id);
		} finally {
			monitorlock.unlock();
		}
	}

	public void nuevoFlujoUsuario(FlujoUsuario fu) {
		monitorlock.lock();
		try {
			flujos.add(fu);
		} finally {
			monitorlock.unlock();
		}
	}

	public ObjectOutputStream getFoutUsuario(String user_id) {
		monitorlock.lock();
		try {
			for (FlujoUsuario flujo : flujos) {
				if (user_id.equals(flujo.getUserId())) {
					return flujo.getFout();
				}
			}
			return null;
		} finally {
			monitorlock.unlock();
		}
	}

	public void delFlujoUsuario(String user_id) {
		monitorlock.lock();
		try {
			flujos.removeIf(flujo -> flujo.getUserId().equals(user_id));
		} finally {
			monitorlock.unlock();
		}
	}

	public String getRutaFichero(String nombre_usuario, String nombre_fichero) {
		monitorlock.lock();
		try {
			for (FileInfo fi : usuarios.get(nombre_usuario).getFicheros()) {
				if (fi.getNombreFichero().equals(nombre_fichero)) {
					return fi.getRuta();
				}
			}
			return null; // No se encontró la ruta del archivo
		} finally {
			monitorlock.unlock();
		}
	}

	public String getIpUsuario(String nombre) {
		monitorlock.lock();
		try {
			Usuario usuario = usuarios.get(nombre);
			return usuario != null ? usuario.getIpUsuario() : null;
		} finally {
			monitorlock.unlock();
		}
	}

}
