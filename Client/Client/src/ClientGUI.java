import java.awt.EventQueue;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.JPasswordField;

public class ClientGUI
{
	private JFrame frame, loginFailFrame, userCreatedFrame, newUserFailFrame;
	private JTextField usernameTextField;
	private JPasswordField passwordField;
	
	/**
	 * Launch the application.
	
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
		
	//This is not needed here. Just was used for hardcoding a couple of passwords
	private String hashPassword(String plainTextPassword)
	{
		String hPass = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
		System.out.println(hPass);
	    return hPass;
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
		usernameTextField.setBounds(86, 54, 172, 26);
		frame.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);
		
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.setMnemonic(KeyEvent.VK_ENTER);
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
					String password = passwordField.getText();
					
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
						okButton.setMnemonic(KeyEvent.VK_ENTER);
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
		//btnNewButton.setBounds(67, 190, 115, 34);
		btnNewButton.setBounds(122, 136, 172, 34);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Username:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel.setBounds(6, 59, 70, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_1.setBounds(6, 96, 70, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() 
		{
		
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String user = usernameTextField.getText();
					String pass = passwordField.getText();
					

					Socket clientSocket = new Socket("127.0.0.1", 8817);
					OutputStream outputStream = clientSocket.getOutputStream();
					String msg = "create " + user +" " + pass + "\n";
					System.out.println(msg);
					outputStream.write(msg.getBytes());
					InputStream inputStream = clientSocket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					String line = reader.readLine();
					System.out.println("clientGUI: line=" + line);
					if (line != null)
					{
						if (line.equals("User Created"))
						{
							userCreatedFrame = new JFrame("User created successfully");
							userCreatedFrame.setBounds(175, 175, 600, 200);
							userCreatedFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
							userCreatedFrame.getContentPane().setLayout(null);
							userCreatedFrame.setVisible(true);
							
							JLabel userCreatedLabel = new JLabel();
							userCreatedLabel.setText("New User Created Successfully");
							userCreatedLabel.setBounds(50, 25, 800, 50);
							userCreatedFrame.getContentPane().add(userCreatedLabel);
							
							JButton okButton2 = new JButton("Ok");
							okButton2.setBounds(250, 75, 50, 25);
							okButton2.setMnemonic(KeyEvent.VK_ENTER);
							userCreatedFrame.getContentPane().add(okButton2); //difference between .getContentPane().add and .add.... both work the same?
							
							okButton2.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									userCreatedFrame.setVisible(false);
								}
							});
						}
						else
						{
							newUserFailFrame = new JFrame("New Account failed");
							newUserFailFrame.setBounds(175, 175, 600, 200);
							newUserFailFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
							newUserFailFrame.getContentPane().setLayout(null);
							newUserFailFrame.setVisible(true);
							
							JLabel userCreatedFailLabel = new JLabel();
							userCreatedFailLabel.setText("Username is already taken");
							userCreatedFailLabel.setBounds(50, 25, 800, 50);
							newUserFailFrame.getContentPane().add(userCreatedFailLabel);
							
							JButton okButton2 = new JButton("Ok");
							okButton2.setBounds(250, 75, 50, 25);
							newUserFailFrame.getContentPane().add(okButton2); //difference between .getContentPane().add and .add.... both work the same?
							
							okButton2.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									newUserFailFrame.setVisible(false);
								}
							});
						}
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
		btnCreate.setBounds(122, 190, 172, 34);
		frame.getContentPane().add(btnCreate);
	
	
		passwordField = new JPasswordField();
		passwordField.setBounds(86, 91, 172, 26);
		frame.getContentPane().add(passwordField);
		
		JCheckBox showPassBox = new JCheckBox("Show Password");
		showPassBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(showPassBox.isSelected())
				{
					passwordField.setEchoChar('\u0000');
				}
				else
				passwordField.setEchoChar('*');
			}
		});
		showPassBox.setBounds(275, 93, 123, 23);
		frame.getContentPane().add(showPassBox);
	}
	
	private boolean Authenticate(String user, String pass, Socket clientSocket) throws IOException//authenticates with server and grants/denies access to HomeGUI
	{
		System.out.println("User is: " + user + " " + "Pass is: " +pass);
		OutputStream outputStream = clientSocket.getOutputStream();
		outputStream.write(("login " + user + " " + pass + "\n").getBytes()); //TODO: get response from server and return true or false based on msg.
		InputStream inputStream = clientSocket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = reader.readLine();
		System.out.print(line);
		if (line != null)
		{
			//System.out.println("ClientGUI, msg received: " + line);
			if (line.equals("yes"))
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
}
