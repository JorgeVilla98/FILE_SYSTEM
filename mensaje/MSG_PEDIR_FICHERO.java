package mensaje;

public class MSG_PEDIR_FICHERO extends Mensaje {
	
	private static final long serialVersionUID = 1L;
	private String nombre_fichero;
	
	public MSG_PEDIR_FICHERO(String origen, String destino, String user_id, String nombre_fichero) {
		super(origen, destino, TipoMensaje.PEDIR_FICHERO);
		this.nombre_fichero = nombre_fichero;
		this.user_id = user_id;
	}

	public String getNombreFichero() {
		return nombre_fichero;
	}
}
