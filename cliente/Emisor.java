package cliente;

import mensaje.MSG_AVISO_EMISOR;
import mensaje.MSG_EMISOR_LISTO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Emisor extends Thread {
	private final String rutaAbsoluta;
	private Cliente cliente;
	private MSG_AVISO_EMISOR mae;

	public Emisor(String rutaAbsoluta, Cliente cliente, MSG_AVISO_EMISOR mae) {
		this.rutaAbsoluta = rutaAbsoluta;
		this.cliente = cliente;
		this.mae = mae;
	}

	public void run() {
		try {
			System.out.println("Se va a enviar el archivo almacenado en: " + rutaAbsoluta);

			// Crear el servidor socket en un try-with-resources para garantizar su cierre
			try (ServerSocket ss = new ServerSocket(0)) {
				System.out.println("Creando conexión en el puerto " + ss.getLocalPort());
				// Enviar información al receptor sobre el puerto en el que se está escuchando
				MSG_EMISOR_LISTO mel = new MSG_EMISOR_LISTO(mae.getIpDestino(), mae.getIpOrigen(), cliente.getUserId(), mae.getIdPeticion(), mae.getNombreFichero(),ss.getLocalPort());
				cliente.enviarMensaje(mel);

				// Esperar a que el receptor se conecte
				try (Socket s = ss.accept();
					 BufferedInputStream fin = new BufferedInputStream(new FileInputStream(rutaAbsoluta));
					 OutputStream fout = s.getOutputStream()) {

					// Leer el archivo y enviarlo al receptor
					byte[] bytes = new byte[16*1024];
					int count;
					while((count = fin.read(bytes)) > 0) {
						fout.write(bytes, 0, count);
					}

					System.out.println("Envío completado.");
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
