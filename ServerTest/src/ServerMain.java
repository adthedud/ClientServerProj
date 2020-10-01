import java.io.File;

public class ServerMain extends Thread 
{
	
	public static void main(String[] args)
	{
		int port = 8817;
		//System.out.println(new File(".").getAbsolutePath());
		Server server = new Server(port);
		server.start();		
	}
}
