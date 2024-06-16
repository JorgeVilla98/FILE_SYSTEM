package utils;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String filename;
	private String path;
	
	public FileInfo(String filename, String path) {
		this.filename = filename;
		this.path = path;
	}
	
	public String getNombreFichero() {
		return this.filename;
	}
	
	public String getRuta() {
		return this.path;
	}

}
