package externalplugins.plugon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/* server class (of typ Thread) which contains a while statement to wait for client
   connections. If a client connects it starts a new Thread with the 
   client socket. */
public class Server extends Thread {
    
    /* initialize the serversocket */
    private ServerSocket serverSocket = null;
        
    /* set the port as an integer */
    private Integer port = 6778; 
        
    /* listenflad set to true , which means server should listen all the time */
    private boolean listening = true;
        
    /* initialize the serverthread */
    public ServerThread svthread = null;
    
    private PlugOn root = null;
    
    /**/
    public Server (PlugOn plugon){
	root = plugon;
    }
    
    /* the normal run() function of all threads */
    public void run() { 
        
        try {
            /* try to create the ServerSocket on port */
            serverSocket = new ServerSocket(port);
            
            serverSocket.setSoTimeout(100);
                        
            System.out.println("Serversocket on Port " + port + " created. ");

	    /* start listening for clients */
	    while (listening) 
		{
		    /* if svthread is null , which means no thread started with a client connection
		       check for clients on the port an accept if one connects. Start a new Thread
		       with the client socket.
		    */
		    if ( svthread == null )
			{
			    try
				{
				    Socket boundedsocket = serverSocket.accept();
				    svthread = new ServerThread(boundedsocket,root);
				    svthread.start();
				} catch ( SocketTimeoutException ste ) {
			    }   
	                           
			}
                          
		    /* check if the clientsocket is still alive , if not set the serverthread back to null */
		    if (svthread != null && svthread.isAlive())
			{
			    try {
				Thread.sleep(100);
			    } catch (InterruptedException ignore) {
				/* ignore */
			    }
			} else {
			svthread = null;
		    }
                          
		}

	    serverSocket.close();
	    System.out.println("Serversocket Closed !");
        
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }
    }
    
    public void stopMe(){    
           
	try
	    {
		if( svthread != null)
		    svthread.sendTo("bye");
                
	    } catch (IOException e) {
	    System.err.println("Could not close the Server Socket");
	    System.exit(-1);
	}
                
	listening = false;                     
    }
           
    
    /* returns the current thread which contains the boundedsocket to the client */
    public ServerThread getThread() {
	return svthread;
    }
           
}
