package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.plugins.medic.data.domain.framework.DataType;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.ui.ExtendedThinlet;

public abstract class ThinletFormField<E>{
	
	/**the ui Controller **/
	protected ExtendedThinlet thinlet;
	/**the label for the box**/
	protected String label;
	/** the associated field **/
	protected MedicField field;	
	/** the main thinlet panel**/
	protected Object mainPanel;
	
	protected E response;

	public ThinletFormField(ExtendedThinlet controller,String label,String name){
		this.thinlet = controller;
		this.label = label;
		mainPanel = controller.createPanel(name);
		controller.setInteger(mainPanel, "columns", 2);
		controller.setInteger(mainPanel, "weightx", 1);
		thinlet.setInteger(mainPanel, "gap", 6);
		if(label != null && label != ""){
			controller.add(mainPanel,controller.createLabel(label));
		}
	}
	
	public ThinletFormField(ExtendedThinlet controller,String label,MedicFormField field, String name){
		this.thinlet = controller;
		this.label = label;
		this.field = field;
		mainPanel = controller.createPanel(name);
		controller.setInteger(mainPanel, "columns", 2);
		controller.add(mainPanel,controller.createLabel(label));
	}
	
	public String getLabel(){
		return label;
	}
	public Object getThinletPanel(){
		return mainPanel;
	}
	
	public abstract boolean isValid();
	
	public boolean hasResponse(){
		return getResponse() !=null;
	}
	
	public E getResponse(){
		return response;
	}
	
	public void setResponse(E response){
		this.response = response;
	}
	
	public MedicField getField(){
		return field;
	}
	
	public void setField(MedicField field){
		this.field = field;
	}
		
	public static ThinletFormField getThinletFormFieldForDataType(DataType t){
		//TODO:make this work
		return null;
	}
}
