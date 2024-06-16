package mensaje;

public class MSG_CONEXION_ERROR extends Mensaje{
    private static final long serialVersionUID = 1L;
    public MSG_CONEXION_ERROR(String origen, String destino, String user_id){
        super(origen, destino, TipoMensaje.CONEXION_ERROR);
        this.user_id = user_id;
    }
}
