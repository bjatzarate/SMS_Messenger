/**
 * @author Billy Joel Arlo T. Zarate
 *
 * This file handles received messages.
 */
package SMS_Messenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Responder
{
	public List<String[]> responselist;
	public List<String[]> registeredlist;
	public List<String[]> messagelist;
	SmsService smsservice;

	public Responder(String response, String register)
	{
		responselist = new ArrayList<String[]>();
		registeredlist = new ArrayList<String[]>();
		messagelist = new ArrayList<String[]>();
		smsservice = MainWindow.smsservice;
		BufferedReader br;
		
		try
		{
			br = new BufferedReader(new FileReader(response));
			String line= "";
			while ((line = br.readLine()) != null)
			{
				// use comma as separator
				String[] content = line.split(",", 2);
				responselist.add(content);
			}
			br.close();
			System.out.println("Response list populated.");
			SmsLogger.log("Response list populated.");
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		try 
		{
			br = new BufferedReader(new FileReader(register));
			String line= "";
			while ((line = br.readLine()) != null)
			{
				// use comma as separator
				String[] content = line.split(",", 2);
				registeredlist.add(content);
			}
			br.close();
			System.out.println("Registered list populated.");
			SmsLogger.log("Registered list populated.");
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}
	
	public void response(String originator, String message)
	{
		String msg = "";
		
		for(int i = 0; i < responselist.size(); i++)
		{
			if(message.matches(responselist.get(i)[0]))
			{
				msg = responselist.get(i)[1];
				messagelist.add(new String[]{originator, msg});
				System.out.println("Auto-reponse. Receiver: " + originator + " Message: " + msg);
				SmsLogger.log("Auto-reponse. Receiver: " + originator + " Message: " + msg);
				String[] split = message.split(" ", 3);
				
				if(split[0].matches("Register"))
				{
					register(new String[]{split[1], split[2], originator});
				}
				break;
			}
		}
		
		if(messagelist.size() > 0)
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

	public void addResponse(String[] content)
	{
		responselist.add(content);
	}
	
	public void addRegistered(String[] content)
	{
		registeredlist.add(content);
	}
	
	public void register(String[] content)
	{
		BufferedWriter bw;
		try
		{
			bw = new BufferedWriter(new FileWriter("registered.csv", true));
			bw.write(content[0] + "," + content[1] + "," + content[2]);
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		registeredlist.add(content);
	}
}
