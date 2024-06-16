package mensaje;

public class MSG_ELIMINAR_FICHERO extends Mensaje{
    private String nombre_fichero;
    public MSG_ELIMINAR_FICHERO(String origen, String destino, String user_id, String nombre_fichero){
        super(origen, destino, TipoMensaje.ELIMINAR_FICHERO);
        this.user_id = user_id;
        this.nombre_fichero = nombre_fichero;
    }

    public String getNombreFichero(){
        return nombre_fichero;
    }
}
