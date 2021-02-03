package ClientThread;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import Server.Server;

public class ClientThread extends Thread{

    //the ClientServiceThread class extends the Thread class and has the following parameters
    public int id;
    public String name; //client name
    public Socket connectionSocket; //client connection socket
    ArrayList<ClientThread> Clients; //list of all clients connected to the server

    //constructor function
    public ClientThread(int id, String name, Socket connectionSocket, ArrayList<ClientThread> Clients) {

        this.id = id;
        this.name = name;
        this.connectionSocket = connectionSocket;
        this.Clients = Clients;

    }

    //thread's run function
    public void run() {

        try {

            //create a buffer reader and connect it to the client's connection socket
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));;
            String clientSentence;
            DataOutputStream outToClient;

            //always read messages from client
            while (true) {

                clientSentence = inFromClient.readLine();

                //ckeck the start of the message

                if (clientSentence.startsWith("-Remove")) { //Remove Client

                    for (int i = 0; i < Clients.size(); i++) {

                        if (Clients.get(i).id == id) {
                            Clients.remove(i);
                        }

                    }

                }   
                else if (clientSentence.startsWith("-Message")) {
               	 String[] messages = clientSentence.split(",");
               	 //the message is successfuly printed here as message[1]
               	 // System.out.println(message[1]);
               	 //if(!sendto[1].equals(" "))
               	 String message=messages[1];
               	 String user = messages[3];
               	Server.messageFrom=user;
               	
               	
               	 if(!(messages[2]).equals(" ")) 
               	 {
               		Server.messageTo=messages[2];
               	 }
               	 else 
               		 Server.messageTo="1";
         
               		Server.message="-NewMessage,"+message+","+ user + ","+Server.messageTo+","+ user ;
               		//System.out.println("message is " + Server.message + " for " + Server.messageTo + " by " + Server.messageFrom);
               	 //
                }
               	 
                
        	 }
        	//the send to person is successfuly printed here System.out.println(sendto[1]);
         		
         		
        	
                
            
            
        
        
    
    
            
        }
        catch(Exception ex) {

        }
    

}
}