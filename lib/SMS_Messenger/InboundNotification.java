/**
 * @author Billy Joel Arlo T. Zarate
 *
 * This file handles received messages.
 */
package SMS_Messenger;

import org.smslib.AGateway;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message;

public class InboundNotification implements IInboundMessageNotification
{
	@Override
	// Get triggered when a SMS is received
	public void process(AGateway gateway, Message.MessageTypes messageTypes, InboundMessage inboundMessage)
	{
		Responder responder = MainWindow.responder;
		String origin = inboundMessage.getOriginator();
		origin = "0" + origin.substring(2, 12);
		String message = inboundMessage.getText().trim();
		System.out.println("From: " + origin + " Message: " + message);
		SmsLogger.log("From: " + origin + " Message: " + message);
		responder.response(origin, message);
		try
		{
			String name = "";
			for(int i = 0 ; i < MainWindow.contactsBTM.getRowCount(); i++)
			{
				if(origin.equals(MainWindow.contactsBTM.getValueAt(i, 0)))
				{
					name = MainWindow.contactsBTM.getValueAt(i, 1).toString().trim();
					break;
				}
			}
			Object[] newrow =
			{
					name, origin, message, inboundMessage.getDate()
			};
			MainWindow.inboxBTM.addRow(newrow);
			MainWindow.inboxtotal.setText("Total messages: " + MainWindow.inboxBTM.getRowCount());
			gateway.deleteMessage(inboundMessage);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
