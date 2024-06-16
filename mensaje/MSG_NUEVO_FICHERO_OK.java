package mensaje;

public class MSG_NUEVO_FICHERO_OK extends Mensaje {
		
	private static final long serialVersionUID = 1L;
	private String nombre_fichero;
	public MSG_NUEVO_FICHERO_OK(String origen, String destino, String nombre_fichero) {
		super(origen, destino, TipoMensaje.NUEVO_FICHERO_OK);
		this.nombre_fichero = nombre_fichero;
	}
	public String getNombreFichero(){
		return nombre_fichero;
	}
}
