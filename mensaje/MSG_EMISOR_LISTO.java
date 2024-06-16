package mensaje;

public class MSG_EMISOR_LISTO extends Mensaje {
	
	private static final long serialVersionUID = 1L;
	private int puerto;
	private String nombre_fichero;
	private String id_peticion;
	public MSG_EMISOR_LISTO(String origen, String destino, String user_id, String id_peticion, String nombre_fichero, int puerto) {
		super(origen, destino, TipoMensaje.EMISOR_LISTO);
		this.puerto = puerto;
		this.user_id = user_id;
		this.id_peticion = id_peticion;
		this.nombre_fichero = nombre_fichero;
	}

	public int getPuerto() {
		return this.puerto;
	}
	
	public String getNombreFichero() {
		return this.nombre_fichero;
    }

	public String getIdPeticion(){return this.id_peticion; }
}
