import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Servidor{
	
	// variavel estatica contendo os usuarios que estao no chat, podendo ser acessada pela ThreadCliente
	public static ArrayList<User> users = new ArrayList<User>();
	
	public static void main(String[] args) {		
		try {
			ServerSocket server = new ServerSocket(2130);
			System.out.println("Servidor rodando...");
			
			// enquanto sempre
			while(true){
				// aceite conexoes com sockets
				Socket socket = server.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				// faca a primeira leitura (que eh o nome do usuario)
				String nome = in.readUTF();
				// instancie um novo usuario
				User u = new User(socket, nome);
				// adicione o novo usuario na lista de usuário do chat
				users.add(u);
				
				// para cada nova conexao matenha uma thread para comunicacao com o cliente
				// enviando como parametros o socket desse usuario, o input, o output e a instancia do usuario
				ThreadCliente tc = new ThreadCliente(socket, in, out, u);
				tc.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}
}
