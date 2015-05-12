import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ThreadEscrita extends Thread{
	Socket socket;
	DataOutputStream out;
	Scanner teclado = new Scanner(System.in);

	public ThreadEscrita(Socket socket, DataOutputStream out){
		this.socket = socket;
		this.out = out;
	}
	
	public void run(){
		while(true){
			String comando = teclado.nextLine();
			
			try {
				out.writeUTF(comando); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}