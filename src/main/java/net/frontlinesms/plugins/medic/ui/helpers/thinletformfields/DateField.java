package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.text.DateFormat;
import java.util.Date;

import net.frontlinesms.plugins.medic.ui.dialogs.DateSelectorDialog;
import net.frontlinesms.ui.ExtendedThinlet;

public class DateField extends ThinletFormField<String>{

	protected Object textBox;
	private DateSelectorDialog ds;
	public static final String NAME = "dateField";
	
	public DateField(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		textBox =thinlet.createTextfield(null, null);
		Object btn = thinlet.create("button");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel,textBox);
		thinlet.add(mainPanel,btn);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setInteger(mainPanel, "columns", 3);
		ds = new DateSelectorDialog(thinlet,textBox);
		thinlet.setAttachedObject(mainPanel, this);
		
	}

	protected DateField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet,label, name);
		textBox =thinlet.createTextfield(null, null);
		Object btn = thinlet.create("button");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel,textBox);
		thinlet.add(mainPanel,btn);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setInteger(mainPanel, "columns", 3);
		ds = new DateSelectorDialog(thinlet,textBox);
	}

	public Date getResponse() {
		return thinlet.getText(textBox);
	}

	public Object getThinletPanel() {
		return mainPanel;
	}

	public boolean hasResponse() {
		return !getResponse().equals("");
	}
	
	public void showDateSelector(){
		try {
			ds.showSelecter();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isValid() {
		try{
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			df.setLenient(false);
			Date date = df.parse(this.getResponse());
		}catch(Exception e){
			return false;
		}
		return true;
	}

	@Override
	public void setResponse(String s) {
		thinlet.setText(textBox,s);
	}

}
