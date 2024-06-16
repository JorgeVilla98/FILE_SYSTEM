package mensaje;

public class MSG_AVISO_RECEPTOR extends Mensaje {
	private static final long serialVersionUID = 1L;
	private String ip_conexion;
	private int puerto_conexion;
	private String filename;
	public MSG_AVISO_RECEPTOR(String origen, String destino, int puerto_conexion, String nombre_fichero) {
		super(origen, destino, TipoMensaje.AVISO_RECEPTOR);
		this.ip_conexion = origen;
		this.puerto_conexion = puerto_conexion;
		this.filename = nombre_fichero;
		
	}
	public int getPuertoConexion() {
		return this.puerto_conexion;
	}
	public String getIpConexion() {
		return this.ip_conexion;
	}
	public String getNombreFichero() {
		return this.filename;
	}
}
