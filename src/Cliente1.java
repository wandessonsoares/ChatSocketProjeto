import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

// Cliente/usuario do chat
public class Cliente1 {
	public static void main(String[] args) {
		
		try {
			Socket socket = new Socket("localhost", 2130);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			// a primeira leitura do servidor eh o nome do cliente
			out.writeUTF("Tiago");
			
			// instancia e starta a thread de leitura
			ThreadLeitura tl = new ThreadLeitura(socket, in);
			tl.start();
			
			// instancia e starta a thread de escrita
			ThreadEscrita te = new ThreadEscrita(socket, out);
			te.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
