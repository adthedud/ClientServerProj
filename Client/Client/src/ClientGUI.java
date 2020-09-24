import java.awt.EventQueue;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class ClientGUI
{
	private JFrame frame, loginFailFrame;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable()  //This is what you have to do to run multiple thread for swing ui?
		{
			public void run() 
			{
				try 
				{
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	private boolean Authenticate(String user, String pass, Socket clientSocket) throws IOException//authenticates with server and grants/denies access to HomeGUI
	{
		System.out.println("User is: " + user + " " + "Pass is: " +pass);
		OutputStream outputStream = clientSocket.getOutputStream();
		outputStream.write(("login " + user + " " + pass + "\n").getBytes()); //TODO: get response from server and return true or false based on msg.
		InputStream inputStream = clientSocket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = reader.readLine();
		System.out.println("ClientGUI: msg from server: " + line);
		if (line != null)
		{
			//System.out.println("ClientGUI, msg received: " + line);
			if (line == "yes")
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		else
		{
			System.out.println("no msg from server recieved :(");
			return false;
		}
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() 
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		frame = new JFrame("Login");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		usernameTextField = new JTextField();
		usernameTextField.setBounds(67, 132, 115, 37);
		frame.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);  //why is this 10?
		
		passwordTextField = new JTextField();
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(236, 132, 115, 37);
		frame.getContentPane().add(passwordTextField);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					//Creates connection to server
					Socket clientSocket = new Socket("127.0.0.1", 8817);
					
					//retrieves username and password from text fields
					String username = usernameTextField.getText();
					String password = passwordTextField.getText();
					
					boolean access = Authenticate(username, password, clientSocket);
					if (access == true)
					{
						HomeGUI homeGUI = new HomeGUI();
						frame.setVisible(false);
						homeGUI.setVisible(true);
						homeGUI.setClientSocket(clientSocket);
						access = false;
					}
					else
					{
						loginFailFrame = new JFrame("Failed Login");
						loginFailFrame.setBounds(175, 175, 600, 200);
						loginFailFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
						loginFailFrame.getContentPane().setLayout(null);
						loginFailFrame.setVisible(true);
						
						JLabel failLoginLabel = new JLabel();
						failLoginLabel.setText("Incorrect username or password you FUCKING RETARD.... uhh duhh durr durr");
						failLoginLabel.setBounds(50, 25, 800, 50);
						loginFailFrame.getContentPane().add(failLoginLabel);
						
						JButton okButton = new JButton("Ok");
						okButton.setBounds(250, 75, 50, 25);
						loginFailFrame.getContentPane().add(okButton); //difference between .getContentPane().add and .add.... both work the same?
						
						okButton.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								loginFailFrame.setVisible(false);
							}
						});
						
					}
				} 
				catch (UnknownHostException e1) 
				{
					e1.printStackTrace();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(142, 180, 172, 34);
		frame.getContentPane().add(btnNewButton);
		
	}
}
