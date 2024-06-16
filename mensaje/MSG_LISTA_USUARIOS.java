package mensaje;

public class MSG_LISTA_USUARIOS extends Mensaje {
	
	private static final long serialVersionUID = 1L;
	
	public MSG_LISTA_USUARIOS(String origen, String destino, String user_id) {
		super(origen, destino, TipoMensaje.LISTA_USUARIOS);
		this.user_id  = user_id;
	}
}
