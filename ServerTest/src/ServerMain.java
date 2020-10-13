import java.io.File;
import java.security.Provider;
import java.security.Security;

public class ServerMain extends Thread 
{	
	public static void main(String[] args)
	{
		//Security.addProvider(new Provider());

		System.setProperty("javax.net.ssl.keyStore", "C:\\Program Files\\Java\\jdk-13.0.2\\bin\\keystore.jks"); //Adam is jdk-13.0.2  Jake is jdk-14.0.2
		System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
//		System.setProperty("javax.net.ssl.trustStore", "C:\Program Files\Java\jdk-14.0.2\bin\\cacerts.jks");
//		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		//System.setProperty("javax.net.debug", "all");
		int port = 8817;
		//System.out.println(new File(".").getAbsolutePath());
		Server server = new Server(port);
		server.start();		
	}
}
