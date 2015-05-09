import java.net.Socket;

// Um POJO de Usuário, com socket e nome correspondentes
public class User {
	private Socket socket;
	private String nome;
	
	public User(Socket socket, String nome) {
		this.socket = socket;
		this.nome = nome;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
