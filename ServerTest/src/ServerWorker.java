import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.*;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONArray;
import org.json.JSONPointerException;
import org.apache.commons.io.FileUtils;
import org.mindrot.jbcrypt.BCrypt;

public class ServerWorker extends Thread
{
	private Socket clientSocket;
	private String login = null;
	private Server server;
	private OutputStream outputStream;
	private HashSet<String> topicSet = new HashSet<>();
	private String authFile = "src\\authenticate.json";

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
					handleLogin(tokens);
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
				else if ("create".equalsIgnoreCase(cmd))
				{
					createUser(tokens);
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
	
	private void handleLogin(String[] tokens) throws IOException 
	{
		if (tokens.length == 3) 
		{
			String login = tokens[1];
			String password = tokens[2];
			
			File file = new File(authFile);
			String content = FileUtils.readFileToString(file, "utf-8");
			JSONObject pointer  = new JSONObject(content);
			//JSONObject creds = pointer.getJSONObject("credentials");
			JSONArray creds = pointer.getJSONArray("credentials");
			String authUser;
			String authPass = null;
			
			//looks through the users in JSON to see which one is the desired account to compare
			for(int i = 0; i < creds.length(); i++)
			{
				authUser = creds.getJSONObject(i).getString("username");
				if (authUser.equals(login))
				{
					authPass = creds.getJSONObject(i).getString("password");
					break;
				}
			}
			
			//if a user is found and the user has the correct password he is then logged in
			//TODO: Eventually get rid of "authPass.equals(password)" and only use the password checking with checkPass
			if (authPass != null && checkPass(password, authPass))
			 {
				String msg = "yes\n";
				try 
				{
					//System.out.print(msg);
					outputStream.write(msg.getBytes());
					this.login = login;
				} 
				catch (IOException e) 
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
				
			}
			else 
			{
				String msg = "no\n";
				try
				{
					outputStream.write(msg.getBytes());
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
	}
		//testing this out
	private void send(String msg) throws IOException
	{
		if(login != null) 
		{
			outputStream.write(msg.getBytes());
		}
		else
		{
			outputStream.write(("Please login first").getBytes());
		}
	}
	
	private boolean checkPass(String plainPassword, String hashedPassword) 
	{
		if (BCrypt.checkpw(plainPassword, hashedPassword))
		{
			System.out.println("The password matches.");
			return true;
		}
		
		else
		{
			System.out.println("The password does not match.");
			return false;
		}
	}
	private void createUser(String[] tokens) throws IOException
	{
		String login = tokens[1];
		String password = tokens[2];
		String authUser;
		
		File file = new File(authFile);
		String content = FileUtils.readFileToString(file, "utf-8");
		JSONObject pointer  = new JSONObject(content);
		JSONArray creds = pointer.getJSONArray("credentials");
		
		
		//looks through the users in JSON to see which one is the desired account to compare
		for (int i = 0; i < creds.length(); i++)
		{
			authUser = creds.getJSONObject(i).getString("username");
			if (authUser.equals(login))
			{
				String msg = "Error username taken\n";
				outputStream.write(msg.getBytes());
				System.out.println("serverworker: username already taken");
				break;
			}
			else
			{
				if (i == creds.length() - 1) //if no user is found
				{
					JSONObject newUser = new JSONObject();
					String hashpw = hashPassword(password);
					newUser.put("username", login);
					newUser.put("password", hashpw);
					creds.put(newUser);
					JSONObject mainObject = new JSONObject();
					mainObject.put("credentials", creds);
					try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(authFile))) 
					{
						mainObject.write(writer);
						writer.write("\n");
						String msg = "User Created\n";
						outputStream.write(msg.getBytes());
						System.out.println("Serverworker: user created, and added to file");
					}
					catch (Exception e)
					{
						System.err.println("you done did fucked up a-aron:\n" + e.getMessage());
					}
				}
				
			}
		}
	}
	
	private String hashPassword(String plainTextPassword)
	{
		String hPass = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
		System.out.println(hPass);
	    return hPass;
	}
}
