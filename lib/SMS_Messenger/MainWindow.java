/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This program serves as the main GUI file of the program
 */
package SMS_Messenger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.smslib.modem.SerialModemGateway;

public class MainWindow implements ActionListener, TableModelListener
{
	public static Responder responder;
	
	JFrame frame;
	JTabbedPane tabs;
	
	JMenuBar menuBar;
	JMenu options, help;
	JMenuItem menuItem;
	/*
	 * JMenuItem startService; JMenuItem stopService;
	 */
	JMenuItem importContacts;
	JMenuItem about;
	
	JButton startService;
	JButton stopService;
	
	JTable contactsTable;
	public static BoxTableModel contactsBTM;
	JTextField entry1;
	JTextArea entry2;
	JButton send;
	JButton bulk;
	
	JTable inboxTable;
	public static BoxTableModel inboxBTM;
	JTextArea inboxText;
	JButton saveInbox;
	JButton deleteInbox;
	public static JLabel inboxtotal;
	JButton reply;
	
	JTable outboxTable;
	public static BoxTableModel outboxBTM;
	JTextArea outboxText;
	JButton saveOutbox;
	JButton deleteOutbox;
	public static JLabel outboxtotal;
	
	JTable sentTable;
	public static BoxTableModel sentBTM;
	JTextArea sentText;
	JButton saveSent;
	JButton deleteSent;
	public static JLabel senttotal;
	
	JTable modemTable;
	public static BoxTableModel modemBTM;
	static SmsService smsservice;
	
	Image phone;
	Icon greenLight;
	Icon redLight;
	Icon composeIcon;
	Icon minbox;
	Icon moutbox;
	Icon msent;
	Icon modemIcon;
	Icon mail;
	Icon bulkmail;
	Icon save;
	Icon delete;
	
	String[] contact =
	{
			"Name", "Number"
	};
	String[] names =
	{
			"Name", "Number", "Message", "Date"
	};
	String[] modemnames =
	{
			"GatewayID", "Com Port"
	};
	
	List<Object[]> emptylist;
	
	List<String[]> messagelist = new ArrayList<String[]>();
	
	public List<String[]> contactslist = new ArrayList<String[]>();
	
	// create window
	public MainWindow()
	{
		try
		{
			phone = ImageIO.read(getClass().getResource("/resources/phone.png"));
			greenLight = new ImageIcon(ImageIO.read(getClass().getResource("/resources/greenlight.png")));
			redLight = new ImageIcon(ImageIO.read(getClass().getResource("/resources/redlight.png")));
			composeIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/compose.png")));
			minbox = new ImageIcon(ImageIO.read(getClass().getResource("/resources/inbox.png")));
			moutbox = new ImageIcon(ImageIO.read(getClass().getResource("/resources/outbox.png")));
			msent = new ImageIcon(ImageIO.read(getClass().getResource("/resources/sent.png")));
			modemIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/modem.png")));
			mail = new ImageIcon(ImageIO.read(getClass().getResource("/resources/mail.png")));
			bulkmail = new ImageIcon(ImageIO.read(getClass().getResource("/resources/bulkmail.png")));
			save = new ImageIcon(ImageIO.read(getClass().getResource("/resources/save.png")));
			delete = new ImageIcon(ImageIO.read(getClass().getResource("/resources/delete.png")));
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
		
		int x = 750;
		int y = 500;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("SMS Messenger");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.setPreferredSize(new Dimension(x, y));
		frame.setLocation((dim.width / 2) - (x / 2), (dim.height / 2) - (y / 2));
		frame.setLayout(new BorderLayout());
		frame.setIconImage(phone);
		
		// create the menubar
		menuBar = new JMenuBar();
		
		options = new JMenu("Options");
		options.setMnemonic(KeyEvent.VK_O);
		options.getAccessibleContext().setAccessibleDescription("Menu for other functionalities.");
		menuBar.add(options);
		
		importContacts = new JMenuItem("Import contacts");
		importContacts.setMnemonic(KeyEvent.VK_I);
		importContacts.getAccessibleContext().setAccessibleDescription("Imports contact details from a file.");
		importContacts.addActionListener(this);
		options.add(importContacts);
		
		help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		help.getAccessibleContext().setAccessibleDescription("Menu for program help.");
		menuBar.add(help);
		
		about = new JMenuItem("About SMS Messenger");
		about.getAccessibleContext().setAccessibleDescription("Program details");
		about.addActionListener(this);
		help.add(about);
		
		frame.setJMenuBar(menuBar);
		
		JPanel startstop = new JPanel(new GridLayout(1, 2));
		startService = new JButton("Start Service", greenLight);
		startService.addActionListener(this);
		startstop.add(startService);
		stopService = new JButton("Stop Service", redLight);
		stopService.addActionListener(this);
		startstop.add(stopService);
		frame.add(startstop, BorderLayout.NORTH);
		
		// tabs
		tabs = new JTabbedPane();
		tabs.setTabPlacement(JTabbedPane.NORTH);
		
		emptylist = new ArrayList<Object[]>();
		
		// create the compose tab
		JPanel compose = new JPanel(new BorderLayout());
		contactsBTM = new BoxTableModel(contact, emptylist);
		contactsTable = new JTable(contactsBTM);
		contactsTable.getModel().addTableModelListener(this);
		JLabel label1 = new JLabel("Reciever");
		entry1 = new JTextField(35);
		JLabel label2 = new JLabel("Message");
		entry2 = new JTextArea(3, 35);
		send = new JButton("Send", mail);
		send.addActionListener(this);
		bulk = new JButton("Bulk", bulkmail);
		bulk.addActionListener(this);
		
		JScrollPane composeScroll = new JScrollPane(contactsTable);
		composeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		composeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contactsTable.setFillsViewportHeight(true);
		JPanel composeForm = new JPanel(new BorderLayout());
		JPanel composeFormWest = new JPanel();
		composeFormWest.setLayout(new BoxLayout(composeFormWest, BoxLayout.Y_AXIS));
		JPanel composeFormEast = new JPanel();
		composeFormEast.setLayout(new BoxLayout(composeFormEast, BoxLayout.Y_AXIS));
		composeFormWest.add(label1);
		composeFormEast.add(entry1);
		composeFormWest.add(label2);
		composeFormEast.add(entry2);
		composeForm.add(composeFormWest, BorderLayout.WEST);
		composeForm.add(composeFormEast, BorderLayout.CENTER);
		JPanel composeButtons = new JPanel(new BorderLayout());
		JPanel composeButtons2 = new JPanel(new GridLayout(1, 2));
		composeButtons2.add(send);
		composeButtons2.add(bulk);
		composeButtons.add(composeForm, BorderLayout.CENTER);
		composeButtons.add(composeButtons2, BorderLayout.SOUTH);
		compose.add(composeScroll, BorderLayout.CENTER);
		compose.add(composeButtons, BorderLayout.NORTH);
		
		tabs.addTab("Compose", composeIcon, compose, "Compose message");
		
		// create the inbox tab
		JPanel inbox = new JPanel(new BorderLayout());
		JPanel iText = new JPanel(new BorderLayout());
		inboxBTM = new BoxTableModel(names, emptylist);
		inboxTable = new JTable(inboxBTM);
		inboxTable.getModel().addTableModelListener(this);
		inboxText = new JTextArea(5, 35);
		saveInbox = new JButton("Save", save);
		saveInbox.addActionListener(this);
		deleteInbox = new JButton("Delete all", delete);
		deleteInbox.addActionListener(this);
		inboxtotal = new JLabel("Total messages: " + inboxBTM.getRowCount());
		inboxTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				if ( e.getClickCount() == 1 )
				{
					final JTable target = (JTable) e.getSource();
					final int row = target.getSelectedRow();
					if ( row >= 0 )
					{
						String name = target.getValueAt(row, 0).toString();
						String number = target.getValueAt(row, 1).toString();
						String message = target.getValueAt(row, 2).toString();
						String date = target.getValueAt(row, 3).toString();
						String string = "";
						if ( name.length() > 0 )
						{
							string = string + name + " (" + number + ")\n";
						}
						else
						{
							string = string + number + "\n";
						}
						string = string + "Date recieved: " + date + "\n";
						string = string + message;
						inboxText.setText(string);
					}
				}
			}
		});
		reply = new JButton("Reply", mail);
		reply.addActionListener(this);
		
		JScrollPane inboxScroll = new JScrollPane(inboxTable);
		inboxScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		inboxScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inboxTable.setFillsViewportHeight(true);
		inboxText.setLineWrap(true);
		inboxText.setWrapStyleWord(true);
		JScrollPane inboxTextScroll = new JScrollPane(inboxText);
		inboxTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inboxText.setEditable(false);
		iText.add(inboxScroll, BorderLayout.CENTER);
		iText.add(inboxTextScroll, BorderLayout.SOUTH);
		inbox.add(iText, BorderLayout.CENTER);
		JPanel inboxButtons = new JPanel(new BorderLayout());
		JPanel inboxButtons2 = new JPanel(new GridLayout(1, 2));
		inboxButtons2.add(saveInbox);
		inboxButtons2.add(deleteInbox);
		inboxButtons.add(reply, BorderLayout.WEST);
		inboxButtons.add(inboxtotal, BorderLayout.EAST);
		inboxButtons.add(inboxButtons2, BorderLayout.SOUTH);
		inbox.add(inboxButtons, BorderLayout.SOUTH);
		
		tabs.addTab("Inbox", minbox, inbox, "View inbox");
		
		// create the outbox tab
		JPanel outbox = new JPanel(new BorderLayout());
		JPanel oText = new JPanel(new BorderLayout());
		outboxBTM = new BoxTableModel(names, emptylist);
		outboxTable = new JTable(outboxBTM);
		outboxText = new JTextArea(5, 35);
		saveOutbox = new JButton("Save", save);
		saveOutbox.addActionListener(this);
		deleteOutbox = new JButton("Delete all", delete);
		deleteOutbox.addActionListener(this);
		outboxtotal = new JLabel("Total messages: " + outboxBTM.getRowCount());
		outboxTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				if ( e.getClickCount() == 1 )
				{
					final JTable target = (JTable) e.getSource();
					final int row = target.getSelectedRow();
					if ( row >= 0 )
					{
						String name = target.getValueAt(row, 0).toString();
						String number = target.getValueAt(row, 1).toString();
						String message = target.getValueAt(row, 2).toString();
						String date = target.getValueAt(row, 3).toString();
						String string = "";
						if ( name.length() > 0 )
						{
							string = string + name + " (" + number + ")\n";
						}
						else
						{
							string = string + number + "\n";
						}
						string = string + "Date recieved: " + date + "\n";
						string = string + message;
						outboxText.setText(string);
					}
				}
			}
		});
		
		JScrollPane outboxScroll = new JScrollPane(outboxTable);
		outboxScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		outboxScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outboxTable.setFillsViewportHeight(true);
		outboxText.setLineWrap(true);
		outboxText.setWrapStyleWord(true);
		JScrollPane outboxTextScroll = new JScrollPane(outboxText);
		outboxTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outboxText.setEditable(false);
		oText.add(outboxScroll, BorderLayout.CENTER);
		oText.add(outboxTextScroll, BorderLayout.SOUTH);
		outbox.add(oText, BorderLayout.CENTER);
		JPanel outboxButtons = new JPanel(new BorderLayout());
		JPanel outboxButtons2 = new JPanel(new GridLayout(1, 2));
		outboxButtons2.add(saveOutbox);
		outboxButtons2.add(deleteOutbox);
		outboxButtons.add(outboxtotal, BorderLayout.EAST);
		outboxButtons.add(outboxButtons2, BorderLayout.SOUTH);
		outbox.add(outboxButtons, BorderLayout.SOUTH);
		
		tabs.addTab("Outbox", moutbox, outbox, "View outbox");
		
		// create the sent items tab
		JPanel sent = new JPanel(new BorderLayout());
		JPanel sText = new JPanel(new BorderLayout());
		sentBTM = new BoxTableModel(names, emptylist);
		sentTable = new JTable(sentBTM);
		sentText = new JTextArea(5, 35);
		saveSent = new JButton("Save", save);
		saveSent.addActionListener(this);
		deleteSent = new JButton("Delete all", delete);
		deleteSent.addActionListener(this);
		senttotal = new JLabel("Total messages: " + sentBTM.getRowCount());
		sentTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				if ( e.getClickCount() == 1 )
				{
					final JTable target = (JTable) e.getSource();
					final int row = target.getSelectedRow();
					if ( row >= 0 )
					{
						String name = target.getValueAt(row, 0).toString();
						String number = target.getValueAt(row, 1).toString();
						String message = target.getValueAt(row, 2).toString();
						String date = target.getValueAt(row, 3).toString();
						String string = "";
						if ( name.length() > 0 )
						{
							string = string + name + " (" + number + ")\n";
						}
						else
						{
							string = string + number + "\n";
						}
						string = string + "Date recieved: " + date + "\n";
						string = string + message;
						sentText.setText(string);
					}
				}
			}
		});
		
		JScrollPane sentscroll = new JScrollPane(sentTable);
		sentscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sentscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sentTable.setFillsViewportHeight(true);
		sentText.setLineWrap(true);
		sentText.setWrapStyleWord(true);
		JScrollPane sentTextScroll = new JScrollPane(sentText);
		sentTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sentText.setEditable(false);
		sText.add(sentscroll, BorderLayout.CENTER);
		sText.add(sentTextScroll, BorderLayout.SOUTH);
		sent.add(sText, BorderLayout.CENTER);
		JPanel sentButtons = new JPanel(new BorderLayout());
		JPanel sentButtons2 = new JPanel(new GridLayout(1, 2));
		sentButtons2.add(saveSent);
		sentButtons2.add(deleteSent);
		sentButtons.add(senttotal, BorderLayout.EAST);
		sentButtons.add(sentButtons2, BorderLayout.SOUTH);
		sent.add(sentButtons, BorderLayout.SOUTH);
		
		tabs.addTab("Sent Items", msent, sent, "View sent messages");
		
		// create the modems tab
		JPanel modem = new JPanel(new BorderLayout());
		JPanel mText = new JPanel(new BorderLayout());
		modemBTM = new BoxTableModel(modemnames, emptylist);
		modemTable = new JTable(modemBTM);
		
		JScrollPane modemscroll = new JScrollPane(modemTable);
		modemscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		modemscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		modemTable.setFillsViewportHeight(true);
		mText.add(modemscroll, BorderLayout.CENTER);
		modem.add(mText, BorderLayout.CENTER);
		
		tabs.addTab("Modems", modemIcon, modem, "View connected modems");
		
		// add all tabs to frame
		frame.add(tabs, BorderLayout.CENTER);
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		System.out.println("Window created.");
		SmsLogger.log("Window created.");
		
		startService.setEnabled(false);
		stopService.setEnabled(false);
		send.setEnabled(false);
		bulk.setEnabled(false);
		
		smsservice = new SmsService();

		if ( smsservice.gatewaycount > 0 )
		{
			SerialModemGateway smg;
			for (int i = 0; i < smsservice.gates.size(); i++)
			{
				smg = smsservice.gates.get(i);
				try
				{
					Object[] gateway =
					{
							smg.getGatewayId(), smg.getGatewayId().substring(0, smg.getGatewayId().length() - 5)
					};
					modemBTM.addRow(gateway);
					System.out.println("Gateway ID: " + smg.getGatewayId() + " added to modemBTM");
					SmsLogger.log("Gateway ID: " + smg.getGatewayId() + " added to modemBTM");
					stopService.setEnabled(true);
					send.setEnabled(true);
					bulk.setEnabled(true);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			
			// auto-response functionality
			responder = new Responder("response.csv", "registered.csv");
			
			JOptionPane.showMessageDialog(frame, "Modem/s found.", "", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			
			JOptionPane.showMessageDialog(frame, "No modem/s found.", "", JOptionPane.INFORMATION_MESSAGE);
		}
		
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				JFrame frame = (JFrame) e.getSource();
				
				if ( smsservice.gatewaycount > 0 && stopService.isEnabled() )
				{
					try
					{
						smsservice.stop();
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				System.out.println("Window closed.");
				SmsLogger.log("Window closed.");
			}
		});
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getSource().equals(send) )
		{
			// send button clicked
			// send message to receiver
			send.setEnabled(false);
			bulk.setEnabled(false);
			String receiver = entry1.getText();
			String message = entry2.getText();
			
			receiver = cleanrecipient(receiver);
			
			if ( receiver.isEmpty() )
			{
				JOptionPane.showMessageDialog(frame, "Reciever field is empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			else if ( !receiver.matches("0*\\d{10}") )
			{
				JOptionPane.showMessageDialog(frame, "Reciever field entry not recognized.", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				// queue message
				DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
				Date dateobj = new Date();
				Object[] rowdata =
				{
						"", receiver.trim(), message.trim(), df.format(dateobj)
				};
				outboxBTM.addRow(rowdata);
				outboxtotal.setText("Total messages: " + outboxBTM.getRowCount());
				String[] data =
				{
						receiver.trim(), message.trim()
				};
				messagelist.add(data);
				System.out.println("Add to queue. Receiver: " + data[0] + " Message: " + data[1]);
				SmsLogger.log("Add to queue. Receiver: " + data[0] + " Message: " + data[1]);
				JOptionPane.showMessageDialog(frame, "Message queued.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
				entry1.setText("");
				entry2.setText("");
				
				if ( stopService.isEnabled() )
				{
					try
					{
						smsservice.sendQueue(messagelist);
						messagelist.clear();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}
			send.setEnabled(true);
			bulk.setEnabled(true);
			tabs.setSelectedIndex(3);
		}
		else if ( e.getSource().equals(bulk) )
		{
			// bulk message
			send.setEnabled(false);
			bulk.setEnabled(false);
			
			JFileChooser chooser;
			FileNameExtensionFilter filter;
			int returnVal;
			DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Date dateobj = new Date();
			
			chooser = new JFileChooser();
			filter = new FileNameExtensionFilter("CSVfiles", "csv");
			chooser.setFileFilter(filter);
			returnVal = chooser.showOpenDialog(frame);
			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				String filename = chooser.getSelectedFile().getAbsolutePath();
				String line = "";
				BufferedReader br;
				
				try
				{
					br = new BufferedReader(new FileReader(filename));
					while ((line = br.readLine()) != null)
					{
						// use comma as separator
						String[] content = line.split(",", 2);
						content[0] = cleanrecipient(content[0]);
						Object[] rowdata =
						{
								"", content[0].trim(), content[1].trim(), df.format(dateobj)
						};
						outboxBTM.addRow(rowdata);
						outboxtotal.setText("Total messages: " + outboxBTM.getRowCount());
						String[] data =
						{
								content[0].trim(), content[1].trim()
						};
						messagelist.add(data);
						System.out.println("Add to queue. Receiver: " + data[0] + " Message: " + data[1]);
						SmsLogger.log("Add to queue. Receiver: " + data[0] + " Message: " + data[1]);
					}
					
					br.close();
					JOptionPane.showMessageDialog(frame, "Messages queued.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				
				if ( stopService.isEnabled() )
				{
					try
					{
						smsservice.sendQueue(messagelist);
						messagelist.clear();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}
			send.setEnabled(true);
			bulk.setEnabled(true);
			tabs.setSelectedIndex(3);
		}
		else if ( e.getSource().equals(reply) )
		{
			int target = inboxTable.getSelectedRow();
			if ( target >= 0 )
			{
				entry1.setText(inboxTable.getValueAt(target, 1).toString());
				tabs.setSelectedIndex(0);
			}
		}
		else if ( e.getSource().equals(saveInbox) )
		{
			JFileChooser chooser;
			FileNameExtensionFilter filter;
			int returnVal;
			
			DateFormat df = new SimpleDateFormat("MMddyyHHmmss");
			Date dateobj = new Date();
			String filename = "Inbox" + df.format(dateobj) + ".csv";
			
			chooser = new JFileChooser();
			filter = new FileNameExtensionFilter("CSVfiles", "csv");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnVal = chooser.showSaveDialog(frame);
			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				try
				{
					PrintWriter writer = new PrintWriter(chooser.getSelectedFile() + "\\" + filename, "UTF-8");
					String buff = "";
					for (int i = 0; i < inboxBTM.getRowCount(); i++)
					{
						buff = "";
						for (int j = 0; j < inboxBTM.getColumnCount(); j++)
						{
							buff = buff + inboxBTM.getValueAt(i, j) + ",";
						}
						writer.println(buff.substring(0, buff.length() - 1));
					}
					writer.close();
					JOptionPane.showMessageDialog(null, "Inbox exported to " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
					SmsLogger.log("Inbox exported to " + filename);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
		else if ( e.getSource().equals(deleteInbox) )
		{
			int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to delete all rows?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if ( n == JOptionPane.YES_OPTION )
			{
				inboxBTM.deleteAll();
				SmsLogger.log("Inbox deleted.");
			}
		}
		else if ( e.getSource().equals(saveOutbox) )
		{
			JFileChooser chooser;
			FileNameExtensionFilter filter;
			int returnVal;
			
			DateFormat df = new SimpleDateFormat("MMddyyHHmmss");
			Date dateobj = new Date();
			String filename = "Outbox" + df.format(dateobj) + ".csv";
			
			chooser = new JFileChooser();
			filter = new FileNameExtensionFilter("CSVfiles", "csv");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnVal = chooser.showSaveDialog(frame);
			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				try
				{
					PrintWriter writer = new PrintWriter(chooser.getSelectedFile() + "\\" + filename, "UTF-8");
					String buff = "";
					for (int i = 0; i < outboxBTM.getRowCount(); i++)
					{
						buff = "";
						for (int j = 0; j < outboxBTM.getColumnCount(); j++)
						{
							buff = buff + outboxBTM.getValueAt(i, j) + ",";
						}
						writer.println(buff.substring(0, buff.length() - 1));
					}
					writer.close();
					JOptionPane.showMessageDialog(null, "Outbox exported to " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
					SmsLogger.log("Outbox exported to " + filename);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
		else if ( e.getSource().equals(deleteOutbox) )
		{
			int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to delete all rows?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if ( n == JOptionPane.YES_OPTION )
			{
				outboxBTM.deleteAll();
				SmsLogger.log("Outbox deleted.");
			}
		}
		else if ( e.getSource().equals(saveSent) )
		{
			JFileChooser chooser;
			FileNameExtensionFilter filter;
			int returnVal;
			
			DateFormat df = new SimpleDateFormat("MMddyyHHmmss");
			Date dateobj = new Date();
			String filename = "Sent" + df.format(dateobj) + ".csv";
			
			chooser = new JFileChooser();
			filter = new FileNameExtensionFilter("CSVfiles", "csv");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnVal = chooser.showSaveDialog(frame);
			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				try
				{
					PrintWriter writer = new PrintWriter(chooser.getSelectedFile() + "\\" + filename, "UTF-8");
					String buff = "";
					for (int i = 0; i < sentBTM.getRowCount(); i++)
					{
						buff = "";
						for (int j = 0; j < sentBTM.getColumnCount(); j++)
						{
							buff = buff + sentBTM.getValueAt(i, j) + ",";
						}
						writer.println(buff.substring(0, buff.length() - 1));
					}
					writer.close();
					JOptionPane.showMessageDialog(null, "Sent items exported to " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
					SmsLogger.log("Sent items exported to " + filename);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
		else if ( e.getSource().equals(deleteSent) )
		{
			int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to delete all rows?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if ( n == JOptionPane.YES_OPTION )
			{
				sentBTM.deleteAll();
				SmsLogger.log("Sent items deleted.");
			}
		}
		else if ( e.getSource().equals(startService) )
		{
			boolean started;
			started = smsservice.start();
			startService.setEnabled(false);
			stopService.setEnabled(true);
			if ( started )
			{
				if ( messagelist.size() > 0 )
				{
					try
					{
						smsservice.sendQueue(messagelist);
						messagelist.clear();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				JOptionPane.showMessageDialog(frame, "Service started.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if ( e.getSource().equals(stopService) )
		{
			boolean stopped;
			stopped = smsservice.stop();
			stopService.setEnabled(false);
			startService.setEnabled(true);
			if ( stopped )
			{
				JOptionPane.showMessageDialog(frame, "Service stopped.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if ( e.getSource().equals(importContacts) )
		{
			JFileChooser chooser;
			FileNameExtensionFilter filter;
			int returnVal;
			
			chooser = new JFileChooser();
			filter = new FileNameExtensionFilter("CSVfiles", "csv");
			chooser.setFileFilter(filter);
			returnVal = chooser.showOpenDialog(frame);
			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				String filename = chooser.getSelectedFile().getAbsolutePath();
				String line = "";
				
				try (BufferedReader br = new BufferedReader(new FileReader(filename)))
				{
					while ((line = br.readLine()) != null)
					{
						// use comma as separator
						String[] content = line.split(",", 2);
						content[0] = cleanrecipient(content[0]);
						String[] rowdata =
						{
								content[0].trim(), content[1].trim()
						};
						contactslist.add(rowdata);
						contactsBTM.addRow(rowdata);
					}
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				
				for (int i = 0; i < contactslist.size(); i++)
				{
					for (int j = 0; j < inboxBTM.getRowCount(); j++)
					{
						if ( inboxBTM.getValueAt(j, 1).toString().equals(contactslist.get(i)[1].toString()) )
						{
							inboxBTM.setValueAt(contactslist.get(i)[0].toString(), j, 0);
						}
					}
					
					for (int j = 0; j < outboxBTM.getRowCount(); j++)
					{
						if ( outboxBTM.getValueAt(j, 1).toString().equals(contactslist.get(i)[1].toString()) )
						{
							outboxBTM.setValueAt(contactslist.get(i)[0].toString(), j, 0);
						}
					}
					
					for (int j = 0; j < sentBTM.getRowCount(); j++)
					{
						if ( sentBTM.getValueAt(j, 1).toString().equals(contactslist.get(i)[1].toString()) )
						{
							sentBTM.setValueAt(contactslist.get(i)[0].toString(), j, 0);
						}
					}
				}
				
				JOptionPane.showMessageDialog(frame, "Contacts added.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
				SmsLogger.log("Contacts added.");
			}
		}
		else if ( e.getSource().equals(about) )
		{
			JOptionPane.showMessageDialog(frame, "SMS Messenger V1.0 by Billy Joel Arlo T. Zarate", "About", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	public String cleanrecipient(String number)
	{
		String pattern1 = "\\d{10}";
		if ( number.matches(pattern1) )
		{
			return "0" + number;
		}
		else
		{
			return number;
		}
	}
	
	public static void deleterow(String number, String text)
	{
		List<Integer> deleterow = new ArrayList<Integer>();
		for (int i = 0; i < outboxBTM.getRowCount(); i++)
		{
			if ( outboxBTM.getValueAt(i, 1).equals(number.trim()) && outboxBTM.getValueAt(i, 2).equals(text.trim()) )
			{
				deleterow.add(i);
			}
		}
		for (int i = 0; i < deleterow.size(); i++)
		{
			outboxBTM.removeRow(deleterow.get(i));
			outboxtotal.setText("Total messages: " + outboxBTM.getRowCount());
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
}