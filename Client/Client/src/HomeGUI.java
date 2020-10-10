import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Button;
import javax.swing.JTextArea;

public class HomeGUI extends JFrame
{
	private JPanel contentPane;
	private JTextArea textField;
	private Socket clientSocket;
	OutputStream outputStream;
	InputStream inputStream;
	private JTextField msgToSendTxtField;
	private String selectedChannel = "World Chat";
	private String channelText = "";
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

		JLabel channelLabel = new JLabel("Channels");
		channelLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		channelLabel.setBounds(10, 11, 122, 14);
		contentPane.add(channelLabel);
		
		textField = new JTextArea();
		textField.setBackground(Color.WHITE);
		textField.setEditable(false);
		textField.setBounds(136, 37, 372, 344);
		contentPane.add(textField);
		textField.setColumns(10);
		try
		{
			channelText = getChannelText("select World Chat\n");
			textField.setText(channelText);
		} 
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		JLabel currentChannelLabel = new JLabel("World Chat");
		currentChannelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentChannelLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		currentChannelLabel.setBounds(136, 13, 372, 23);
		contentPane.add(currentChannelLabel);
		
		//channelList is the list of channels, includes listener to change text when channel is selected
		JList channelList = new JList();
		ListSelectionListener listListener = new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				boolean adjusting = e.getValueIsAdjusting();
				if (!adjusting)
				{
					selectedChannel = channelList.getSelectedValue().toString();
					System.out.println(selectedChannel);
					currentChannelLabel.setText(selectedChannel);
					String msg = "select " + selectedChannel + "\n";
					try
					{
						channelText = getChannelText(msg);
						textField.setText(channelText);
					} 
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		};
	
		
		//This Button Joins a selected channel when clicked
		JButton createChannelButton = new JButton("Create Channel");
		createChannelButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		createChannelButton.addActionListener(new ActionListener() //TODO: Need to change these to implement create channel instead of get channel
		{
			public void actionPerformed(ActionEvent e) 
			{
//				int channelIndex;
//				channelIndex = channelList.getSelectedIndex();
//				currentChannelLabel.setText(channelList.getSelectedValue().toString());
			}
		});
		createChannelButton.setBounds(10, 335, 106, 23);
		contentPane.add(createChannelButton);
		
		//This Button is disconnects from the Server and closes application
		JButton logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					outputStream.write(("quit\n").getBytes());
					//clientSocket.close(); Server should do this in handleLogout
					HomeGUI.this.setVisible(false);
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		logoutButton.setBounds(553, 358, 89, 23);
		contentPane.add(logoutButton);
		
		JButton sendMsgButton = new JButton("Send");
		sendMsgButton.setBounds(508, 389, 85, 21);
		contentPane.add(sendMsgButton);
		
		msgToSendTxtField = new JTextField();
		msgToSendTxtField.setBounds(136, 390, 372, 19);
		contentPane.add(msgToSendTxtField);
		msgToSendTxtField.setColumns(10);
		
		JList<String> friendList = new JList<String>();
		friendList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		friendList.setModel(new AbstractListModel() 
		{
			String[] values = new String[] {"Adam", "Jake"};
			public int getSize() 
			{
				return values.length;
			}
			public Object getElementAt(int index) 
			{
				return values[index];
			}
		});
		friendList.setBounds(531, 40, 145, 202);
		contentPane.add(friendList);
		
		JButton addFriendButton = new JButton("Add Friend");
		addFriendButton.setBounds(553, 252, 106, 23);
		contentPane.add(addFriendButton);
		
		JButton removeFriendButton = new JButton("Remove Friend");
		removeFriendButton.setBounds(553, 288, 106, 23);
		contentPane.add(removeFriendButton);
		
		JButton leaveChannelButton = new JButton("Leave Channel");
		leaveChannelButton.setBounds(10, 389, 106, 21);
		contentPane.add(leaveChannelButton);
		
		JButton joinChannelButton = new JButton("Join Channel");
		joinChannelButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		joinChannelButton.setBounds(10, 362, 106, 23);
		contentPane.add(joinChannelButton);
		
		JLabel friendslistLabel = new JLabel("Friends List");
		friendslistLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		friendslistLabel.setBounds(537, 14, 122, 14);
		contentPane.add(friendslistLabel);
		
		JButton retrieveTextFileBtn = new JButton("Retrieve Text file");
		retrieveTextFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OutputStream outputStream;
				try {
					outputStream = clientSocket.getOutputStream();
					outputStream.write(("select\n").getBytes()); //TODO: get response from server and return true or false based on msg.
					InputStream inputStream = clientSocket.getInputStream();
					DataInputStream dis = new DataInputStream(clientSocket.getInputStream()); 
					String k = "";
					String fullTextFile = "";
					while(!(k = dis.readUTF()).equals("EndOfFile\n"))
                    {          
						if(!k.equals("EndOfFile"))
						{
							fullTextFile += k + "\n";
	                        System.out.println(k);
						}						
                    }					
					textField.setText(fullTextFile);
                    System.out.println("File Transferred");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		retrieveTextFileBtn.setFont(new Font("Tahoma", Font.PLAIN, 8));
		retrieveTextFileBtn.setBounds(515, 322, 116, 23);
		contentPane.add(retrieveTextFileBtn);
	}
	
	//sets this client socket to the client socket from ClientGUI
	public void setClientSocket(Socket socket)
	{
		this.clientSocket = socket;
		try 
		{
			outputStream = clientSocket.getOutputStream();
			inputStream = clientSocket.getInputStream();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public String getChannelText(String msg) throws IOException
	{
		outputStream.write(msg.getBytes());
		inputStream = clientSocket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line, channelText = null;	
		while ((line = reader.readLine()) != null)
		{
			channelText += line;
		}
		return channelText;
	}
}
