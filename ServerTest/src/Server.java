import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Server extends Thread
{
	private ArrayList<ServerWorker> workerList = new ArrayList<>();
	private final int serverPort;
	
	private static final String[] protocols = new String[] {"TLSv1.3"};
    private static final String[] cipher_suites = new String[] {"TLS_AES_128_GCM_SHA256"};
	
	public Server(int serverPort) 
	{
		this.serverPort = serverPort;
	}
	
	public List<ServerWorker> getWorkerList()
	{
		return workerList;
		 
		
	}

	@Override
	public void run()
	{
		try 
		{
			//ServerSocket serverSocket = new ServerSocket(serverPort);
			
			SSLServerSocketFactory sslssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		    SSLServerSocket sslServerSocket = (SSLServerSocket) sslssf.createServerSocket(serverPort);
		    sslServerSocket.setEnabledProtocols(protocols);
	        sslServerSocket.setEnabledCipherSuites(cipher_suites);
		        
			
//			SSLSocket serverSocket = createSocket("localhost", serverPort);
			
			
			while(true)
			{					
				SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();
				//SSLSocket clientSocket = (SSLSocket) serverSocke;	//Accepts clients connection here
				//System.out.println("Client Connected from " + clientSocket);
				//Starts a new Thread
				ServerWorker worker = new ServerWorker(this, clientSocket);
				workerList.add(worker);
				
				
				worker.start();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static SSLSocket createSocket(String host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault()
                .createSocket(host, port);
        socket.setEnabledProtocols(protocols);
        socket.setEnabledCipherSuites(cipher_suites);
        return socket;
    }
	
	public void removeWorker(ServerWorker serverWorker)
	{
		workerList.remove(serverWorker);
	}

}
