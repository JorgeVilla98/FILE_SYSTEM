package mensaje;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected String origen;
	protected String destino;
	protected String user_id;
	private TipoMensaje tipo;
	public Mensaje(String origen, String destino, TipoMensaje tipo) {
		this.origen = origen;
		this.destino = destino;
		this.tipo = tipo;
	}
	public String getIpOrigen() {
		return this.origen;
	}
	public String getIpDestino() {
		return this.destino;
	}
	public String getIdUsuario(){
		return this.user_id;
	}
	public TipoMensaje getTipo(){return tipo;}
	
}
