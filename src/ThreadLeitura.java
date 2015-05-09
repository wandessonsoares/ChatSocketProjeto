import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// Thread para ficar ficar recebendo as mensagens do servidor a qualquer momento
public class ThreadLeitura extends Thread{
	Socket socket;
	DataInputStream in;

	public ThreadLeitura(Socket socket, DataInputStream in){
		this.socket = socket;
		this.in = in;
	}
	
	public void run(){
		
		// loop infinito
		while(true){
			try {
				System.out.println(in.readUTF()); // somente exibe no console a mensagem
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
