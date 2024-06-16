package mensaje;

public class MSG_PEDIR_FICHERO_ERROR extends Mensaje{
    private static final long serialVersionUID = 1L;
    private String nombre_fichero;
    private boolean propioUser;
    public MSG_PEDIR_FICHERO_ERROR(String origen, String destino, String user_id, String nombre_fichero, boolean propioUser) {
        super(origen,destino, TipoMensaje.PEDIR_FICHERO_ERROR);
        this.user_id= user_id;
        this.nombre_fichero = nombre_fichero;
        this.propioUser = propioUser;
    }
    public String getNombreFichero(){
        return nombre_fichero;
    }
    public boolean getPropioUser(){return propioUser;}
}
