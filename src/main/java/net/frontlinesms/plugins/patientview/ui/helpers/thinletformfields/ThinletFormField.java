package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
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
	/** the string that represents the response of the field **/
	protected String response;

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
	
	/** returns the label of the field **/
	public String getLabel(){
		return label;
	}
	
	/**returns the main panel of the field that contains all the subcomponents **/
	public Object getThinletPanel(){
		return mainPanel;
	}
	
	/**expansion point for validating the fields **/
	public abstract boolean isValid();
	
	/**returns true if the field currently has a response **/
	public boolean hasResponse(){
		return getResponse() !=null && getResponse()!="";
	}
	
	/**returns the current response in string form 
	 * the default is string form because it needs to be database readable**/
	public abstract String getResponse();
	
	/** sets the response with a string **/
	public abstract void setResponse(String response);
	
	/**gets the response in the field's designated format **/
	public abstract E getRawResponse();
	
	public abstract void setRawResponse(E response);
	
	
	public MedicField getField(){
		return field;
	}
	
	public void setField(MedicField field){
		this.field = field;
	}
	
	public void setEditable(boolean editable){
		thinlet.setEditableRecursively(getThinletPanel(), editable);
	}
	
	public void setEnabled(boolean editable){
		thinlet.setEnabledRecursively(getThinletPanel(), editable);
	}
		
	public static ThinletFormField getThinletFormFieldForDataType(DataType t){
		//TODO:make this work
		return null;
	}
}
