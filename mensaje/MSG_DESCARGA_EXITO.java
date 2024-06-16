package mensaje;

public class MSG_DESCARGA_EXITO extends Mensaje{
    private String nombre_fichero;
    private String ruta;
    public MSG_DESCARGA_EXITO(String origen, String destino, String user_id, String nombre_fichero, String ruta){
        super(origen, destino, TipoMensaje.DESCARGA_EXITO);
        this.nombre_fichero = nombre_fichero;
        this.ruta = ruta;
        this.user_id = user_id;
    }
    public String getNombreFichero(){
        return nombre_fichero;
    }
    public String getRuta(){
        return ruta;
    }
}
