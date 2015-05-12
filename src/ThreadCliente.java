import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
		
		broadcastInfo("** " + user.getNome() + " esta online agora.");

		while(true){
			try {
				comando = in.readUTF();
				String resposta = interpretador(comando);
				
//				if(sair == true){
//					out.writeUTF(resposta);
//					socket.close();
//				}
				
				out.writeUTF(resposta);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String interpretador(String comando){
		
		// lista os usuarios que estao online
		if(comando.equals("list")){
			
			StringBuffer usuariosOnline = new StringBuffer();
			
			for (User u : Servidor.users) {
				usuariosOnline.append("\n -" + u.getNome());
			}
			
			return "\n======================\n"
					+ "Usuarios online: " + usuariosOnline
					+ "\n======================";
		}
		
		// envia uma mensagem para todos que estao online
		else if(comando.contains("send -all")){
			String mensagem = comando.substring(9);
			
			String emitente = null;
			for (User u : Servidor.users) {
				if(u.getSocket() == socket){
					emitente = u.getNome();
				}
			}
			
			broadcast(mensagem, emitente);
			return "";
		}
		
		// envia uma mensagem para apenas um usuario especifico (mensagem privada)
		else if(comando.contains("send -user")){
			String temp = comando.substring(11);
			String user = temp.substring(0, temp.indexOf(" "));
			String msg = temp.substring(temp.indexOf(" "));
			
			User remetente = null;
			for (User u : Servidor.users) {
				if (u.getNome().equals(user)) {
					remetente = u;
				}
			}
			
			String emitente = null;
			for (User u : Servidor.users) {
				if (u.getSocket() == socket) {
					emitente = u.getNome();
				}
			}
			
			sendTo(remetente, emitente, msg);
			return "";
		}
		
		// renomeia o nome do usuario atual
		else if(comando.contains("rename")){
			String novonome = comando.substring(7);
			boolean usado = false;
			
			for (User u : Servidor.users) {
				if(novonome.equals(u.getNome())){
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
			
			broadcastInfo("** " + this.user.getNome() + " saiu.");
			Servidor.users.remove(user);
			ThreadCliente.this.stop();
			
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			return "";
		}
		
		else{
			return "** Comando não reconhecido pelo servidor.";
		}
	}
	
	public void broadcast(String msg, String emitente){
		for (User u : Servidor.users) {
			try {
				String horaFormatada = retornaHora();
				String dataFormatada = retornaData();
				
				DataOutputStream o = new DataOutputStream(u.getSocket().getOutputStream());
				o.writeUTF("\n" + socket.getInetAddress() + ":" + socket.getPort() + "/~" + emitente + ":" + msg + " " + horaFormatada + " " + dataFormatada);
			} catch (IOException e) {
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
				e.printStackTrace();
			}
		}
	}
	
	// envia uma mensagem para um usuario especifico
	public void sendTo(User remetente, String emitente, String msg){
		String dataFormatada = retornaData();
		String horaFormatada = retornaHora();
		
		try {
			if(remetente == null){
				DataOutputStream o = new DataOutputStream(socket.getOutputStream());
				o.writeUTF("\nUsuario inexistente.");
			}
			else{
				DataOutputStream o = new DataOutputStream(remetente.getSocket().getOutputStream());
				o.writeUTF("\n" + socket.getInetAddress() + ":" + socket.getPort() + "/~" + emitente + ":" + msg + " " + horaFormatada + " " + dataFormatada);
			}
		} catch (IOException e) {
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
