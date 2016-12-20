package org.magic.gui.components;

import java.util.List;

import org.japura.gui.CheckComboBox;

public class JCheckableListBox<T> extends CheckComboBox {
	
	
	public JCheckableListBox() {
		super();
	}
	
	public List<T> getSelectedElements()
	{
		return (List<T>) getModel().getCheckeds();
	}
	
	public void setSelectedElements(List<T> elements)
	{
		for(T e : elements)
			getModel().addCheck(e);
		
	}
	
	public void unselectAll()
	{
		getModel().removeChecks();
	}
	
	

}
