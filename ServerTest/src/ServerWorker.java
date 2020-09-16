import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.*;

//import com.sun.tools.javac.util.StringUtils;;


public class ServerWorker extends Thread
{

	private Socket clientSocket;
	private String login = null;
	private Server server;
	private OutputStream outputStream;
	private HashSet<String> topicSet = new HashSet<>();

	public ServerWorker(Server server, Socket clientSocket) 
	{
		this.server = server;
		this.clientSocket = clientSocket;
	}

	public void run() 
	{
		try 
		{
			handleClientSocket();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	

	
	private void handleClientSocket() throws IOException, InterruptedException 
	{
		InputStream inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();	
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while((line = reader.readLine()) != null) 
		{
			String[] tokens = StringUtils.split(line);
			if(tokens != null && tokens.length > 0) 
			{
				String cmd = tokens[0];
				if("quit".equalsIgnoreCase(cmd)) 
				{
					handleLogout();
					break;
				}
				else if ("login".equalsIgnoreCase(cmd)) 
				{
					handleLogin(outputStream, tokens);
				}
				else if ("msg".equalsIgnoreCase(cmd))
				{
					String[] tokensMsg = StringUtils.split(line, null, 3);
					handleMessage(tokensMsg);
				}
				else if ("join".equalsIgnoreCase(cmd))
				{
					handleJoin(tokens);
				}
				else if ("leave".equalsIgnoreCase(cmd))
				{
					handleLeave(tokens);
				}
				else 
				{
					String msg = "Unknown " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}		
			}			
		}
	}
	
	private void handleLeave(String[] tokens) 
	{
		if(tokens.length > 1)
		{
			if(tokens[1].charAt(0) == '#')
			{
				topicSet.remove(tokens[1]);	
			}
		}
			
	}

	public boolean isMemberOfTopic(String topic)
	{
		return topicSet.contains(topic);
	}
	
	private void handleJoin(String[] tokens) 
	{
		if (tokens.length > 1)
		{
			
			String topic = tokens[1];
			topicSet.add(topic);
		}
			
		
	}

	// msg #topic body
	private void handleMessage(String[] tokens) throws IOException
	{
		String sendTo = tokens[1];
		String body = tokens[2];
		
		boolean isTopic = sendTo.charAt(0) == '#';
		
		List<ServerWorker> workerList = server.getWorkerList();
		for( ServerWorker worker : workerList)
		{
			if(isTopic)
			{
				if (worker.isMemberOfTopic(sendTo))
				{
					String outMsg = sendTo + " msg from "+  login + ": " + body + "\n";
					worker.send(outMsg);
				}
			}
			if (sendTo.equalsIgnoreCase(worker.getLogin()))
			{
				String outMsg = "msg from " + login + ": " + body + "\n";
				worker.send(outMsg);
			}
		}
	}
	
	private void handleLogout() throws IOException
	{	
		server.removeWorker(this);
		List<ServerWorker> workerList = server.getWorkerList();
		String offlineMsg = login + " is now offline!\n";
		
		//send all users that this user is now offline
		for(ServerWorker worker : workerList) 
		{
			if(!login.equals(worker.getLogin())) 
			{						
				worker.send(offlineMsg);												
			}					
		}
		clientSocket.close();
	}

	public String getLogin() 
	{
		return login;
	}
	
	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException 
	{
		if (tokens.length == 3) 
		{
			String login = tokens[1];
			String password = tokens[2];
			
			if ((login.equals("guest") && password.equals("guest"))|| (login.equalsIgnoreCase("Adam") && password.equals("test")) ) 
			{
				String msg = "Logged in as " + login + "\n";
				try 
				{
					outputStream.write(msg.getBytes());
					this.login = login;
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				String onlineMsg = login + " is now online!\n";
				List<ServerWorker> workerList = server.getWorkerList();
				
				//sends the current user all other online logins
				for(ServerWorker worker : workerList) 
				{
					if(worker.getLogin() != null)
					{
						if(!login.equals(worker.getLogin())) 
						{
							String msg2 = worker.getLogin() + " is online!\n";
							send(msg2);
						}						
					}
				}
				
				//send all users that this user is now online
				for(ServerWorker worker : workerList) 
				{
					if(!login.equals(worker.getLogin())) 
					{						
						worker.send(onlineMsg);												
					}					
				}
				
			}else {
				String msg = "Error logging in\n";
				try {
					outputStream.write(msg.getBytes());
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
	}

	private void send(String msg) throws IOException
	{
		if(login != null) 
		{
			outputStream.write(msg.getBytes());
		}		
	}
}
