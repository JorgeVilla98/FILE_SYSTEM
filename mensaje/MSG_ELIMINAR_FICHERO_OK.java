package mensaje;

public class MSG_ELIMINAR_FICHERO_OK extends Mensaje {
    private String nombre_fichero;
    private boolean ok;
    public MSG_ELIMINAR_FICHERO_OK(String origen, String destino, String nombre_fichero, boolean ok){
        super(origen, destino, TipoMensaje.ELIMINAR_FICHERO_OK);
        this.nombre_fichero = nombre_fichero;
        this.ok = ok;
    }

    public boolean isOk(){return ok;}
    public String getNombreFichero(){return nombre_fichero;}
}
