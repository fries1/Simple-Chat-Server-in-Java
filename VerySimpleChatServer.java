import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class VerySimpleChatServer {
	ArrayList<PrintWriter> clients;
	ArrayList<ObjectOutputStream> outputStreams;
	public class ClientHandler implements Runnable {
		// Constructor takes a client socket
		// creates a reader
		// reads client message and calls tellEveryone() method to promote new client msg to all clients
		Socket sock;
		
		public ClientHandler(Socket s){
			sock = s;
		}
		
		public void run() {
			try {
				System.out.println("Waiting for messages");
				//InputStreamReader inStream = new InputStreamReader(sock.getInputStream());
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				UserData user = (UserData) ois.readObject();
				System.out.println(user.userName + " just connected");
				//tellEveryone(user.userName + " entered the room.");
				tellEveryone2(new Message(user.userName, " entered the room"));
				//BufferedReader reader = new BufferedReader(inStream);
				//String msg;
				/*
				while((msg = reader.readLine()) != null){
					System.out.println("received a msg from client: " + msg);
					msg = "read: " + msg;
					tellEveryone(msg);
				}
				*/
				Message m;
				while((m = (Message) ois.readObject()) != null){
					System.out.println("received a msg form client " + m.userName + ": " + m.message);
					//tellEveryone(m.userName + ": " + m.message);
					tellEveryone2(m);
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		// creates new sever obj and calls its go method
		VerySimpleChatServer myChatServer = new VerySimpleChatServer();
		myChatServer.go();
	}
	
	public void go(){
		// holds a Arraylist with all clients
		// creates new server socket that waits in while loop for new clients to connect
		// when a client connects, a new thread is created with the sever sock passed to its constructor
		try {
			
			System.out.println(Inet4Address.getLocalHost().getHostAddress() + " 5000");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		clients = new ArrayList<PrintWriter>();
		outputStreams = new ArrayList<ObjectOutputStream>();
		try {
			ServerSocket sock = new ServerSocket(5000);
			System.out.println("Listening for clients to connect");
			while(true){
				Socket s = sock.accept();
				PrintWriter writer = new PrintWriter(s.getOutputStream());
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				Thread client = new Thread(new ClientHandler(s));
				client.start();
				clients.add(writer);
				outputStreams.add(oos);
				System.out.println("got a client");
				//tellEveryone(s.toString() + " entered the room");
			}	
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	/*
	public void tellEveryone(String message){
		// distributes the message to all clients in the list,
		
		for(PrintWriter writer: clients){
			try {
				//System.out.println(socket.getLocalAddress());
				//PrintWriter writer = new PrintWriter(socket.getOutputStream());
				writer.println(message);
				writer.flush();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	*/
	
	public void tellEveryone2(Message m){
		// distributes the message to all clients in the list,

		for(ObjectOutputStream s : outputStreams){
			try{
				s.writeObject(m);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
}
/*
class UserData implements Serializable{


	String userName;
	public UserData(String userName){
		this.userName = userName;
	}
}
*/