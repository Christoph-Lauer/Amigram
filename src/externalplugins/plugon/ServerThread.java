package externalplugins.plugon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* Class ServerThread does all the transfer things between the
   Server an the Client socket */
public class ServerThread extends Thread {
       
    private Socket socket = null;
    private PrintWriter out = null;
    private boolean active = true;
    private PlugOn root = null;

    public ServerThread(Socket ssocket, PlugOn plugon) {
	super("ServerThread");
	this.socket = ssocket;
	root = plugon;
    }

    
    
    public void run() {

	try {
	    System.out.println("Connected to Client");
        
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
        String inputLine, outputLine;

        /* wait for input from the client */
        while ((inputLine = in.readLine()) != null && active == true) {
                root.setData(inputLine);
        }
        
        out.close();
        in.close();
        socket.close();
        
        } catch (IOException e) {
            /* if client has closed and the error connection reset occur,
               stop this thread (depreciated) */
         if( e.getMessage().startsWith( "Connection reset" ) == true )
             {
             if (root.getStatus() == true)
                 root.waitswitch();
                 
                 System.out.println("Connection to Client lost !");
                 active = false;
             }
	 //e.printStackTrace();
	    }
    }
    
    public void sendTo(String str) throws IOException{
           out = new PrintWriter(socket.getOutputStream(), true);
           out.println(str);
         }
}
