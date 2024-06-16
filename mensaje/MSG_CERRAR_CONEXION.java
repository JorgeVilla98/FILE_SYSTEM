package mensaje;

public class MSG_CERRAR_CONEXION extends Mensaje {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public MSG_CERRAR_CONEXION(String origen, String destino, String user_id) {
		super(origen, destino,TipoMensaje.CERRAR_CONEXION);
		this.user_id = user_id;
	}

}
