package mensaje;

public class MSG_CONEXION_CLIENTE extends Mensaje {

	private static final long serialVersionUID = 1L;

	public MSG_CONEXION_CLIENTE(String origen, String destino, String user_id) {
		super(origen,destino, TipoMensaje.CONEXION_CLIENTE);
		this.user_id =	user_id;
	}

}
