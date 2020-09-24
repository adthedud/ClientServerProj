import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class HomeGUI extends JFrame
{

	private JPanel contentPane;
	private JTextField textField;
	private Socket clientSocket;
	OutputStream outputStream;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					HomeGUI frame = new HomeGUI();
					frame.setVisible(true);
					
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")  //added to suppress warning at line 70 channellist.setModel(...)
	public HomeGUI() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 447);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JList channelList = new JList();
		channelList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		channelList.setModel(new AbstractListModel() 
		{
			String[] values = new String[] {"Sub1", "Sub2"};
			public int getSize() 
			{
				return values.length;
			}
			public Object getElementAt(int index) 
			{
				return values[index];
			}
		});
		channelList.setBounds(10, 36, 106, 316);
		contentPane.add(channelList);
		
		JLabel channelLabel = new JLabel("Channels");
		channelLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		channelLabel.setBounds(10, 11, 122, 14);
		contentPane.add(channelLabel);
		
		textField = new JTextField();
		textField.setBackground(Color.WHITE);
		textField.setEditable(false);
		textField.setBounds(136, 37, 372, 344);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel currentChannelLabel = new JLabel("Select a Channel");
		currentChannelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentChannelLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		currentChannelLabel.setBounds(136, 13, 372, 23);
		contentPane.add(currentChannelLabel);
		
		//This Button Joins a selected channel when clicked
		JButton joinChannelButton = new JButton("Join Channel");
		joinChannelButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		joinChannelButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				int channelIndex;
				channelIndex = channelList.getSelectedIndex();
				currentChannelLabel.setText(channelList.getSelectedValue().toString());
			}
		});
		joinChannelButton.setBounds(10, 358, 106, 23);
		contentPane.add(joinChannelButton);
		
		//This Button is disconnects from the Server and closes application
		JButton logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					outputStream.write(("quit\n").getBytes());
					clientSocket.close();
					HomeGUI.this.setVisible(false);
					
					
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		logoutButton.setBounds(599, 0, 89, 23);
		contentPane.add(logoutButton);
		
	}
	
	//sets this client socket to the client socket from ClientGUI
	public void setClientSocket(Socket socket)
	{
		this.clientSocket = socket;
		try 
		{
			outputStream = clientSocket.getOutputStream();			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
}
