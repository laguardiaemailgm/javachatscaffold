package chat_version_4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer implements Runnable {
	
	private int clientCount =0;
	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket server = null;
	Thread thread = null;
	
	//same as version3
	public ChatServer(int port){
		try{
			server = new ServerSocket(port);//step1
			System.out.println("Started the server...waiting for a client");
			start(); //the chatserver's start method that goes ahead and creates a new thread
		}
		catch(IOException e){
			System.err.println("ERROR "+e.getMessage());
			
		}
	}
	
	public void start(){
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {//same as version 3
		while(thread !=null){
			try{
				System.out.println("Waiting for a client...");
				//now we add a new Thread and accept a client
				addThread(server.accept());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

	}
	
	public void addThread(Socket socket){
		if(clientCount < clients.length){
		   clients[clientCount] = new ChatServerThread(this, socket);
			try {
				 clients[clientCount].open();//open the stream for the ChatServerThread client
				 clients[clientCount].start();//start to run the ChatServerThread client
				 clientCount++;
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		}
	}

	//Working
	public synchronized void handle(int ID, String input){


		for(int i=0; i<clientCount; i++){
			//add line of code to print the user's message
			//on the server side for spying
			clients[i].send("User: "+ ID + ": "+input);

		}
		if(input.equalsIgnoreCase("bye")){
			remove(ID);//person said bye so remove them
		}

	}
	
	public synchronized void remove(int ID){
		int position = findClient(ID);
		if(position >=0){
			ChatServerThread toRemove = clients[position];
			if(position <clientCount-1){
				for(int i= position+1; i <clientCount; i++){
					clients[i-1] = clients[i];
				}
				clientCount--;
			}
			try {
				toRemove.close();//close the person's that said bye connection
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
	}
	private int findClient(int ID){
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				return i;
			}
		}
		return -1;//not in the array
	}
	
	public static void main(String [] args){
		ChatServer myServer = null;
		if(args.length !=1){
			System.out.println("You need to specify a port number!!!");
		}
		else{
			int portNum = Integer.parseInt(args[0]);
			myServer = new ChatServer(portNum);//create an instance of my ChatServer
		}
	}
	
	

}
