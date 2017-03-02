/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This program serves as the main connection to the modem.
 */
package SMS_Messenger;

import gnu.io.CommPortIdentifier;

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
	List<SerialModemGateway> gates;
	int gatewaycount = 0;
	
	public SmsService()
	{
		gates = new ArrayList<SerialModemGateway>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> pp = CommPortIdentifier.getPortIdentifiers();
		if ( pp.hasMoreElements() )
		{
			while (pp.hasMoreElements())
			{
				CommPortIdentifier ne = pp.nextElement();
				if(ne.getPortType() == CommPortIdentifier.PORT_SERIAL && ne.getName().matches("COM\\d+") && !ne.getName().matches("COM1"))
				{
					gateway = new SerialModemGateway(ne.getName() + "modem", ne.getName(), 115200, "", "");
					gateway.setInbound(true);
					gateway.setOutbound(true);
					gates.add(gateway);
					gatewaycount++;
					System.out.println("Gateway " + ne.getName() + "modem created.");
					SmsLogger.log("Gateway " + ne.getName() + "modem created.");
					System.out.println("gatewaycount: " + gatewaycount);
					SmsLogger.log("gatewaycount: " + gatewaycount);
				}	
			}
			
			outboundNotification = new OutboundNotification();
			inboundNotification = new InboundNotification();
			
			service = Service.getInstance();
			service.setOutboundMessageNotification(outboundNotification);
			service.setInboundMessageNotification(inboundNotification);
			System.out.println("Service created.");
			SmsLogger.log("Service created.");
			
			for (int i = 0; i < gatewaycount; i++)
			{
				try
				{
					service.addGateway(gates.get(i));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				System.out.println("Gateway added.");
				SmsLogger.log("Gateway added.");
			}
			
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
