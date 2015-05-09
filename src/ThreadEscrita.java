import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// Thread para que o cliente possa enviar um comando para o servidor qualquer momento 
public class ThreadEscrita extends Thread{
	Socket socket;
	DataOutputStream out;
	Scanner teclado = new Scanner(System.in);

	public ThreadEscrita(Socket socket, DataOutputStream out){
		this.socket = socket;
		this.out = out;
	}
	
	public void run(){
		
		// loop infinito
		while(true){
			String comando = teclado.nextLine(); // le uma string do teclado
			
			try {
				out.writeUTF(comando); // envia a string lida para o servidor
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
