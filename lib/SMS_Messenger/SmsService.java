/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This program serves as the main connection to the modem.
 */
package SMS_Messenger;

import gnu.io.CommPortIdentifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;

import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class SmsService
{
	SerialModemGateway gateway;
	Service service;
	OutboundNotification outboundNotification;
	InboundNotification inboundNotification;
	String[] gatedetails = {"", "", "", ""};
	int gatewaycount = 0;
	
	public SmsService()
	{
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> pp = CommPortIdentifier.getPortIdentifiers();
		if ( pp.hasMoreElements() )
		{
			String line = "";
			String[] banana;
			BufferedReader br;
			
			try
			{
				br = new BufferedReader(new FileReader("Modem.ini"));
				while ((line = br.readLine()) != null)
				{
					banana = line.split(": ", 2);
					switch(banana[0])
					{
						case "Modem_Port":
						{
							gatedetails[0] = banana[1];
							break;
						}
						case "Baud_Rate":
						{
							gatedetails[1] = banana[1];
							break;
						}
						case "Manufacturer":
						{
							gatedetails[2] = banana[1];
							break;
						}
						case "Model":
						{
							gatedetails[3] = banana[1];
							break;
						}
					}
				}
				br.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			gateway = new SerialModemGateway(gatedetails[0] + "modem", gatedetails[0], Integer.parseInt(gatedetails[1]), gatedetails[2], gatedetails[3]);
			gatewaycount++;
			gateway.setInbound(true);
			gateway.setOutbound(true);
			System.out.println("Gateway " + gatedetails[0] + "modem created.");
			SmsLogger.log("Gateway " + gatedetails[0] + "modem created.");
			System.out.println("gatewaycount: " + gatewaycount);
			SmsLogger.log("gatewaycount: " + gatewaycount);
			
			
			outboundNotification = new OutboundNotification();
			inboundNotification = new InboundNotification();
			
			service = Service.getInstance();
			service.setOutboundMessageNotification(outboundNotification);
			service.setInboundMessageNotification(inboundNotification);
			System.out.println("Service created.");
			SmsLogger.log("Service created.");
			
			try
			{
				service.addGateway(gateway);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Gateway added.");
			SmsLogger.log("Gateway added.");
			
			try
			{
				service.startService(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Service started.");
			SmsLogger.log("Service started.");
		}
	}
	
	public void sendMessage(String receipient, String text) throws Exception
	{
		OutboundMessage msg = new OutboundMessage(receipient, text);
		service.queueMessage(msg);
	}
	
	public void sendQueue(List<String[]> list) throws Exception
	{
		// bulk message
		List<OutboundMessage> msg = new ArrayList<OutboundMessage>();
		for (int i = 0; i < list.size(); i++)
		{
			msg.add(new OutboundMessage(list.get(i)[0], list.get(i)[1]));
		}
		service.queueMessages(msg);
	}
	
	public boolean start()
	{
		if ( gatewaycount > 0 )
		{
			try
			{
				service.startService();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Service started.");
			SmsLogger.log("Service started.");
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "No service to start.", "ERROR", JOptionPane.ERROR_MESSAGE);
			SmsLogger.log("No service to start.");
			return false;
		}
	}
	
	public boolean stop()
	{
		if ( gatewaycount > 0 )
		{
			try
			{
				service.stopService();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Service stopped.");
			SmsLogger.log("Service stopped.");
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "No service to stop.", "ERROR", JOptionPane.ERROR_MESSAGE);
			SmsLogger.log("No service to stop.");
			return false;
		}
	}
}
