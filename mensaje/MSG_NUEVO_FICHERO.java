package mensaje;

public class MSG_NUEVO_FICHERO extends Mensaje {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre_fichero;
	private String ruta;
	
	
	public MSG_NUEVO_FICHERO(String origen, String destino, String user_id, String nombre_fichero, String ruta) {
		super(origen, destino, TipoMensaje.NUEVO_FICHERO);
		this.nombre_fichero = nombre_fichero;
		this.user_id = user_id;
		this.ruta = ruta;
	}

	public String getNombreFichero() {
		return this.nombre_fichero;
	}

	public String getRuta() {
		return ruta;
	}
}
