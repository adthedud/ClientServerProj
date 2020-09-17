import java.awt.EventQueue;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextField;

public class ClientGUI {

	
	private JFrame frame;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		
		usernameTextField = new JTextField();
		usernameTextField.setBounds(67, 132, 115, 37);
		frame.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);
		
		passwordTextField = new JTextField();
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(236, 132, 115, 37);
		frame.getContentPane().add(passwordTextField);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Creates connection to server
					Socket clientSocket = new Socket("127.0.0.1", 8818);
					OutputStream outputStream = clientSocket.getOutputStream();
					
					//retrieves username and password from text fields
					String username = usernameTextField.getText();
					String password = passwordTextField.getText();
					
					//logs in the user
					outputStream.write(("login " + username + " " + password + "\n").getBytes());
					HomeGUI homeGUI = new HomeGUI();
					homeGUI.setVisible(true);
					frame.setVisible(false);
					
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(142, 180, 172, 34);
		frame.getContentPane().add(btnNewButton);
	}
}
