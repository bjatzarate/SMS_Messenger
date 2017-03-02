/**
 * @author Billy Joel Arlo T. Zarate
 * 
 * This class overrides the TableModel class
 */
package SMS_Messenger;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class BoxTableModel extends DefaultTableModel
{
	private String[] columnNames;
	private List<Object[]> data = new ArrayList<Object[]>();
	
	public BoxTableModel(String[] names, List<Object[]> initdata)
	{
		columnNames = names;
		if ( !initdata.equals(null) )
		{
			data.addAll(initdata);
		}
	}
	
	public int getColumnCount()
	{
		return columnNames.length;
	}
	
	public int getRowCount()
	{
		if ( data == null )
		{
			return 0;
		}
		else
		{
			return data.size();
		}
	}
	
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
	public Object getValueAt(int row, int col)
	{
		return data.get(row)[col];
	}
	
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public void addRow(Object[] rowdata)
	{
		data.add(rowdata);
		fireTableDataChanged();
	}
	
	public void removeRow(int row)
	{
	    data.remove(row);
	    fireTableRowsDeleted(row, row);
	}
	
	public void deleteAll()
	{
		data.clear();
		fireTableDataChanged();
	}
}
