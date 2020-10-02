import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

public class ModifyJSON
{
	private String authFile = "src\\authenticate.json";
	public ModifyJSON()
	{
		
	}
	
	//Returns desired JSONArray
	public JSONArray getJSONArray(String field, String user) throws IOException
	{
		File file = new File(authFile);
		String content = FileUtils.readFileToString(file, "utf-8");
		JSONObject pointer  = new JSONObject(content);
		JSONArray creds = pointer.getJSONArray("credentials");
		
		if (field.equalsIgnoreCase("credentials"))
		{
			return creds;
		}
		else if (field.equalsIgnoreCase("friendsList") || field.equalsIgnoreCase("channels"))
		{
			return getArraySubset(creds, user, field);
		}
		else
		{
			return null;			
		}
	}
	
	//Returns channels or friendsList JSONArray
	private JSONArray getArraySubset(JSONArray creds, String user, String field)
	{
		String username;
		JSONArray array = null;
		for (int i = 0; i < creds.length(); i ++)
		{
			username = creds.getJSONObject(i).getString("username"); 
			if (username.equals(user))
			{
				array = creds.getJSONObject(i).getJSONArray(field);
				break;
			}
		}
		return array;
	}
	
	//Returns desired value from JSONObject (user name or password)
	public String getValue(String field, String username, JSONArray creds)
	{
		String authUser = null;
		String authPass = null;
		String compareUser = null;
		for(int i = 0; i < creds.length(); i++)
		{
			compareUser = creds.getJSONObject(i).getString("username");
			if (compareUser.equals(username)) //TODO: Make sure users can add same name with different capitals i.e. adam Adam aDAM
			{
				authUser = creds.getJSONObject(i).getString("username");
				authPass = creds.getJSONObject(i).getString("password");
				break;
			}	
		}
		if (field.equals("username"))
			return authUser;
		if (field.equals("password"))
			return authPass;
		else 
			return null;
	}
	
	//Creates a new JSONObject (user) and adds their credentials to authenticate.json file
	public void createJSONObject(String user, String pass, JSONArray creds) 
	{
		JSONObject newUser = new JSONObject();
		JSONArray newFriends = new JSONArray();
		JSONArray newChannel = new JSONArray();
		String hashpw = hashPassword(pass);
		newUser.put("username", user);
		newUser.put("password", hashpw);
		newUser.put("friendsList", newFriends);
		newUser.put("channels", newChannel);
		creds.put(newUser);
		JSONObject mainObject = new JSONObject();
		mainObject.put("credentials", creds);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(authFile))) //writing to json
		{
			mainObject.write(writer);
			writer.write("\n");
			System.out.println("Serverworker: user created, and added to file");
		}
		catch (Exception e)
		{
			System.err.println("you done did fucked up a-aron:\n" + e.getMessage());
		}
	}
	
	//Encrypts password for storing in JSON file
	private String hashPassword(String plainTextPassword)
	{
		String hPass = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
		System.out.println(hPass);
	    return hPass;
	}
}