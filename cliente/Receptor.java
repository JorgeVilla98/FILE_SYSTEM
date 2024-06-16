package cliente;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

public class Receptor extends Thread {
	private final String ipConexion;
	private final int puertoConexion;
	private final String nombreFichero;
	private final String file_extension;
	private final String ruta;
	private final int comp;
	private final Cliente cliente;

	public Receptor(String ipConexion, int puertoConexion, String nombreFicheroExt, String ruta, Cliente cliente) {
		this.ipConexion = ipConexion;
		this.puertoConexion = puertoConexion;
		this.nombreFichero = nombreFicheroExt.substring(0, nombreFicheroExt.lastIndexOf('.'));
		this.file_extension = nombreFicheroExt.substring(nombreFicheroExt.lastIndexOf('.'));
		this.ruta = ruta;
		this.comp = new Random().nextInt(100); // Para evitar conflictos de nombres
		this.cliente = cliente;
	}

    public void run() {
        System.out.println("Emisor esperando desde " + ipConexion + ":" + puertoConexion);
        try (Socket socket = new Socket(ipConexion, puertoConexion);
             InputStream fin = socket.getInputStream()) {

            // Usar un nombre de archivo único para evitar conflictos
            String nombreFinal = nombreFichero + comp + file_extension;
            File newfile = new File(ruta + File.separator + nombreFinal);

            // Verificar si el archivo ya existe
            if (newfile.exists()) {
                throw new IOException("El archivo ya existe: " + nombreFinal);
            }

            // Crear el archivo y escribir en él los datos recibidos
            try (BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(newfile))) {
                byte[] bytes = new byte[16 * 1024];
                int count;
                while ((count = fin.read(bytes)) > 0) {
                    fout.write(bytes, 0, count);
                }
                System.out.println("Archivo descargado y creado correctamente.");
                cliente.notificarDescargaFichero(ruta, nombreFinal);
            } catch (IOException e) {
                System.err.println("Error al escribir en el archivo: " + e.getMessage());
                if (newfile.exists()) {
                    newfile.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al establecer la conexión con el emisor", e);
        }
    }

}
