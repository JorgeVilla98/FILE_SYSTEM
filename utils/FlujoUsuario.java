package utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FlujoUsuario implements Serializable{

	private static final long serialVersionUID = 1L;
	private String user_id;
	private ObjectOutputStream fout;
	private ObjectInputStream fin;
	
	public FlujoUsuario(String id, ObjectOutputStream fout, ObjectInputStream fin) {
		super();
		this.user_id = id;
		this.fout = fout;
		this.fin = fin;
	}

	public String getUserId() {
		return user_id;
	}

	public ObjectOutputStream getFout() {
		return fout;
	}

	public ObjectInputStream getFin() {
		return fin;
	}
}
