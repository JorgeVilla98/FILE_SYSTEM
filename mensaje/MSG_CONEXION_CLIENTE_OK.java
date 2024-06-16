package mensaje;

public class MSG_CONEXION_CLIENTE_OK extends Mensaje {
	
	private static final long serialVersionUID = 1L;

	public MSG_CONEXION_CLIENTE_OK(String origen, String destino) {
		super(origen, destino, TipoMensaje.CONEXION_OK);
	}
}
