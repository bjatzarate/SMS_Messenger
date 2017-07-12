/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This file handles queued messages.
 */
package SMS_Messenger;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.OutboundMessage.MessageStatuses;

public class OutboundNotification implements IOutboundMessageNotification
{
	@Override
	public void process(AGateway gateway, OutboundMessage outboundMessage)
	{
		System.out.println("Status: " + outboundMessage.getMessageStatus() + " Recipient: " + outboundMessage.getRecipient() + " Message: " + outboundMessage.getText());
		SmsLogger.log("Status: " + outboundMessage.getMessageStatus() + " Recipient: " + outboundMessage.getRecipient() + " Message: " + outboundMessage.getText());
		if ( outboundMessage.getMessageStatus().equals(MessageStatuses.FAILED) )
		{
			JOptionPane.showMessageDialog(null, "Status: " + outboundMessage.getMessageStatus() + " Recipient: " + outboundMessage.getRecipient() + " Message: " + outboundMessage.getText(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			String name = "";
			for(int i = 0 ; i < MainWindow.contactsBTM.getRowCount(); i++)
			{
				if(outboundMessage.getRecipient().trim().equals(MainWindow.contactsBTM.getValueAt(i, 0)))
				{
					name = MainWindow.contactsBTM.getValueAt(i, 1).toString().trim();
					break;
				}
			}
			Object[] newrow =
			{
					name, outboundMessage.getRecipient().trim(), outboundMessage.getText().trim(), outboundMessage.getDispatchDate()
			};
			MainWindow.sentBTM.addRow(newrow);
			MainWindow.senttotal.setText("Total messages: " + MainWindow.sentBTM.getRowCount());
			
			List<Integer> deleterow = new ArrayList<Integer>();
			for(int i = 0; i < MainWindow.outboxBTM.getRowCount(); i++)
			{
				if(MainWindow.outboxBTM.getValueAt(i, 1).equals(outboundMessage.getRecipient().trim()) && MainWindow.outboxBTM.getValueAt(i, 2).equals(outboundMessage.getText().trim()))
				{
					deleterow.add(i);
				}
			}
			for(int i = 0; i < deleterow.size(); i++)
			{
				MainWindow.outboxBTM.removeRow(deleterow.get(i));
				MainWindow.outboxtotal.setText("Total messages: " + MainWindow.outboxBTM.getRowCount());
			}
		}
	}
	
}
