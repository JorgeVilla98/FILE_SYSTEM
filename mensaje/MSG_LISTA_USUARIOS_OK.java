package mensaje;

import java.util.ArrayList;

import utils.Usuario;

public class MSG_LISTA_USUARIOS_OK extends Mensaje {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Usuario> usuarios;
	
	public MSG_LISTA_USUARIOS_OK(String origen, String destino, ArrayList<Usuario> usuarios) {
		super(origen, destino, TipoMensaje.LISTA_USUARIOS_OK);
		this.usuarios = usuarios;
	}
	public ArrayList<Usuario> getListaUsuarios() {
		return this.usuarios;
	}

}
