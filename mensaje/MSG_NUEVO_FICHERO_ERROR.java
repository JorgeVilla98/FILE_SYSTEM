package mensaje;

public class MSG_NUEVO_FICHERO_ERROR extends Mensaje{
    private String nombre_fichero;
    public MSG_NUEVO_FICHERO_ERROR(String origen, String destino, String nombre_fichero){
        super(origen,destino,TipoMensaje.NUEVO_FICHERO_ERROR);
        this.nombre_fichero = nombre_fichero;
    }
}
