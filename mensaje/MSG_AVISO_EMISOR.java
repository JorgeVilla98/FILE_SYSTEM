package mensaje;

public class MSG_AVISO_EMISOR extends Mensaje {
	private static final long serialVersionUID = 1L;
	private String nombre_fichero;
	private String ruta;
	private String id_peticion;
	public MSG_AVISO_EMISOR(String origen, String destino, String id_peticion, String nombre_fichero, String ruta) {
		super(origen , destino, TipoMensaje.AVISO_EMISOR);
		this.ruta = ruta;
		this.nombre_fichero = nombre_fichero;
		this.id_peticion = id_peticion;
	}
	public String getRuta() {
		return this.ruta;
	}
	public String getNombreFichero() {
		return this.nombre_fichero;
	}
	public String getIdPeticion(){return id_peticion;}
	
}
