/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This program serves as the logger of the program.
 */
package SMS_Messenger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsLogger
{
	static BufferedWriter smslogs;
	static String line;
	
	public static void log(String log)
	{		
		try
		{
			DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Date dateobj = new Date();
			
			smslogs = new BufferedWriter(new FileWriter("SMSLogs.txt", true));
			line = df.format(dateobj) + " " + log;
			smslogs.write(line);
			smslogs.newLine();
			smslogs.flush();
		}
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		finally
		{ // always close the file
			if ( smslogs != null )
			{
				try
				{
					smslogs.close();
				}
				catch (IOException ioe2)
				{
					// just ignore it
				}
			}
		}
	}
}
