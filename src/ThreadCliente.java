import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// principal classe da aplicacao, pois contem toda a conversacao com os clientes
public class ThreadCliente extends Thread{
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	User user;
	String comando;
	boolean sair = false;

	public ThreadCliente(Socket socket, DataInputStream in, DataOutputStream out, User user){
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.user = user;
	}
	
	public void run(){
		
		// avisa a todos que estao online que esse cliente entrou no chat
		// ver metodo broadcastInfo
		broadcastInfo("** " + user.getNome() + " esta online agora.");

		// enquanto sempre
		while(true){
			try {
				// leia o comando que o cliente mandar
				comando = in.readUTF();
				// guarde o retorno do metodo interpretador na variavel resposta
				String resposta = interpretador(comando);
				
				// caso o comando seja sair
				if(sair == true){
					out.writeUTF(resposta);
					// encerre a comunicacao com o socket
					socket.close();
				}
				
				// envie a resposta para o cliente
				out.writeUTF(resposta);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// interpreta os comandos enviados pelo cliente
	public String interpretador(String comando){
		
		// lista os usuarios que estao online
		if(comando.equals("list")){
			
			StringBuffer usuariosOnline = new StringBuffer();
			
			// varre a lista de usuarios e adiciona em usuariosOnline
			for (User u : Servidor.users) {
				usuariosOnline.append("\n -" + u.getNome());
			}
			
			// retorna a lista formatada
			return "\n======================\n"
					+ "Usuarios online: " + usuariosOnline
					+ "\n======================";
		}
		
		// envia uma mensagem para todos que estao online
		else if(comando.contains("send -all")){
			// separa a mensagem do comando "send-all"
			String mensagem = comando.substring(9);
			
			// salva quem esta enviando a mensagem
			String emitente = null;
			for (User u : Servidor.users) {
				// se o socket dessa thread for igual a de um usuario na lista
				if(u.getSocket() == socket){
					// o emitente eh ele
					emitente = u.getNome();
				}
			}
			
			// ver metodo broadcast
			broadcast(mensagem, emitente);
			return "";
		}
		
		// envia uma mensagem para apenas um usuario especifico (mensagem privada)
		else if(comando.contains("send -user")){
			// salva tudo que esta depois de "send-user"
			String temp = comando.substring(11);
			// pega o nome do usuário que esta a partir do primeiro indice de temp ate o espaço em branco
			String user = temp.substring(0, temp.indexOf(" "));
			// pega a mensagem que esta depois do espaço em branco de temp
			String msg = temp.substring(temp.indexOf(" "));
			
			// grava o usuario que ira receber a mensagem
			User remetente = null;
			for (User u : Servidor.users) {
				// se o o nome do usuario dessa thread for igual ao informado no comando
				if (u.getNome().equals(user)) {
					// o remetente eh ele
					remetente = u;
				}
			}
			
			String emitente = null;
			for (User u : Servidor.users) {
				if (u.getSocket() == socket) {
					emitente = u.getNome();
				}
			}
			
			// ver metodo sendTo
			sendTo(remetente, emitente, msg);
			return("");
		}
		
		// renomeia o nome do usuario atual
		else if(comando.contains("rename")){
			// pega o novo nome, que esta depois de rename
			String novonome = comando.substring(7);
			// flag para saber se esse nome ja esta sendo usado
			boolean usado = false;
			
			for (User u : Servidor.users) {
				// se o novo nome for igual a algum nome de usuario
				if(novonome.equals(u.getNome())){
					// esse nome ja esta sendo usado
					usado = true;
				}
			}
			
			if(usado == true){
				return "\n**Nome de usuario ja em uso.";
			}
			else{
				this.user.setNome(novonome);
				return "\n**Renomeado com sucesso.";
			}
		}
		
		// remove o usuario do chat e encerra a comunicacao com o mesmo (fechando o socket)
		else if(comando.equals("bye")){
			
			// informa para os que estao online que o usuario saiu
			broadcastInfo("** " + this.user.getNome() + " saiu.");
			// remove o usuario da lista de usuarios que estao no chat
			Servidor.users.remove(user);
			// encerra a thread com o cliente
			ThreadCliente.this.stop();
			
			try {
				// fecha o socket
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			return "";
		}
		
		// avisa se o comando nao for nenhum dos que eram esperados
		else{
			return "** Comando não reconhecido pelo servidor.";
		}
	}
	
	// envia uma mensagem no formato estabelecido para todos
	public void broadcast(String msg, String emitente){
		for (User u : Servidor.users) {
			try {
				// ver metodo retornaHora
				String horaFormatada = retornaHora();
				// ver metodo retornaData
				String dataFormatada = retornaData();
				
				// instancia o output do usuario dessa iteracao
				DataOutputStream o = new DataOutputStream(u.getSocket().getOutputStream());
				// envia a mensagem formatada para ele
				o.writeUTF("\n" + socket.getInetAddress() + ":" + socket.getPort() + "/~" + emitente + ":" + msg + " " + horaFormatada + " " + dataFormatada);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// envia uma informacao para todos os usuarios que estao online
	public void broadcastInfo(String msg){
		for (User u : Servidor.users) {
			try {				
				DataOutputStream o = new DataOutputStream(u.getSocket().getOutputStream());
				o.writeUTF(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// envia uma mensagem para um usuario especifico
	public void sendTo(User remetente, String emitente, String msg){
		String dataFormatada = retornaData();
		String horaFormatada = retornaHora();
		
		try {
			// informa caso o usuario nao exista
			if(remetente == null){
				DataOutputStream o = new DataOutputStream(socket.getOutputStream());
				o.writeUTF("\nUsuario inexistente.");
			}
			// caso contrario envia a mensagem para ele
			else{
				// instancia o output do remetente
				DataOutputStream o = new DataOutputStream(remetente.getSocket().getOutputStream());
				o.writeUTF("\n" + socket.getInetAddress() + ":" + socket.getPort() + "/~" + emitente + ":" + msg + " " + horaFormatada + " " + dataFormatada);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// formata a hora para o formato solicitado. ex: 3h00
	public String retornaHora(){
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat hora = new SimpleDateFormat("HH");
		SimpleDateFormat minuto = new SimpleDateFormat("mm");
		Date date = new Date();
		calendar.setTime(date);
		return hora.format(calendar.getTime()) + "h" + minuto.format(calendar.getTime());
	}
	
	// formata a data para o formato solicitado. ex: 11/03/2015
	public String retornaData(){
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		calendar.setTime(date);
		return df.format(calendar.getTime());
	}
}
