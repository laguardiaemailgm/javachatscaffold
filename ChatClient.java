package chat_version_4;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class ChatClient implements Runnable{

	private Socket socket = null;
	protected BufferedReader console = null;
	private DataOutputStream strOut = null;
	private DataInputStream strIn = null;
	private Thread thread = null;
	ChatClientThread client = null;

	String line = "";
	
	public ChatClient(String serverName, int serverPort){
		try {
			System.out.println("Will try to connect to "+serverName+" at "+serverPort);
			socket = new Socket(serverName, serverPort);//step1 try to connect to server on port number
			System.out.println("WE CONNECTED TO SERVER:"+serverName);
			start();//step2 connect to input and connect to output
		} catch (UnknownHostException e) {
			System.out.println("Unknown host "+e.getMessage());
		} catch (IOException e) {
			System.err.println("IO Exception "+e.getMessage());
		}
	}
	
	
	@Override
	public void run() {
		while((thread!=null) &&(!line.equalsIgnoreCase("BYE") )){//until user says bye
			try {
				line = console.readLine();//read line from them
				strOut.writeUTF(line);//send it to the server
				strOut.flush();

			} catch (IOException e) {
				System.err.println("IO Exception inside ChatClient run method " + e.getMessage());
				
			}
		}
	}

	public void start() throws IOException{
		console = new BufferedReader(new InputStreamReader(System.in));
		strOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null){
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void handle(String msg){
		/*
		 * if the user says bye 
		 * call the ChatClient's stop method
		 * 
		 * else print the msg to the console
		 * so it can be read on the server spying
		 */

		if(!msg.equalsIgnoreCase("BYE")){
			try {
				this.stop();
			} catch (IOException e) {
				System.err.println("IO Exception inside ChatClient if_handle method " + e.getMessage());
			}
		}
		else
			try {
				strOut.writeUTF(msg);
			} catch (IOException e) {
				System.err.println("IO Exception inside ChatClient else_handle method " + e.getMessage());
			}

	}


	public void stop() throws IOException {
		/*set the done flag to true
		 * nullify the streams, sockets, ChatThreadClient
		 */
		if(strIn!=null){
			strIn.close();
		}
		if(strOut!=null){
			strOut.close();
		}
		if(socket!=null){
			socket.close();
		}
		//Depreciated
		/*
		if(client != null){
			client.stop();
		}*/
	}

	public static void main(String[] args){
		ChatClient client = null;
		if(args.length !=2){
			System.out.println("To chat you must specify both a host and a port ");
		}
		else{
			client = new ChatClient(args[0],Integer.parseInt(args[1]));
		}
	}
	
}
