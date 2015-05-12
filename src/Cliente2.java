import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente2 {
	public static void main(String[] args) {

		try {
			Socket socket = new Socket("localhost", 2130);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			out.writeUTF("Alex");
			
			ThreadLeitura tl = new ThreadLeitura(socket, in);
			tl.start();
			
			ThreadEscrita te = new ThreadEscrita(socket, out);
			te.start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}