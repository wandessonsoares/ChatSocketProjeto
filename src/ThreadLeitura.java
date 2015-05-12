import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ThreadLeitura extends Thread{
	Socket socket;
	DataInputStream in;

	public ThreadLeitura(Socket socket, DataInputStream in){
		this.socket = socket;
		this.in = in;
	}
	
	public void run(){
		while(true){
			try {
				System.out.println(in.readUTF()); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}