/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This program serves as the main file of the program
 */
package SMS_Messenger;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class MainProgram
{
	public static void initializeFontSize()
	{
		float multiplier = 1.25f;
		UIDefaults defaults = UIManager.getDefaults();
		Enumeration<Object> e = defaults.keys();
		while (e.hasMoreElements())
		{
			Object key = e.nextElement();
			Object value = defaults.get(key);
			if ( value instanceof Font )
			{
				Font font = (Font) value;
				int newSize = Math.round(font.getSize() * multiplier);
				if ( value instanceof FontUIResource )
				{
					defaults.put(key, new FontUIResource(font.getName(), font.getStyle(), newSize));
				}
				else
				{
					defaults.put(key, new Font(font.getName(), font.getStyle(), newSize));
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		initializeFontSize();
		new MainWindow();
	}
	
}
