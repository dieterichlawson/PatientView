package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.text.DateFormat;
import java.util.Date;

import net.frontlinesms.ui.ExtendedThinlet;

public class BirthdateField extends DateField{
	
	protected boolean hasChanged;
	private DateFormat df;
	public static final String NAME = "birthDatefield";
	
	public BirthdateField(ExtendedThinlet thinlet, Date initialDate) {
		super(thinlet, "Birthdate:", NAME);
		hasChanged = false;		
		df = DateFormat.getDateInstance(DateFormat.SHORT);
		if(initialDate != null){
			String initialText = df.format(initialDate);
			thinlet.setText(textBox, initialText);
		}
		thinlet.setAction(textBox, "textChanged(this.text)", null, this);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	public void textChanged(String text){
		hasChanged = true;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}

}
