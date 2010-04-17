package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PasswordTextField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PhoneNumberField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.UiGeneratorController;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

public abstract class ThinletFormField<E>{
	
	/**the ui Controller **/
	protected ExtendedThinlet thinlet;
	/**the label for the box**/
	protected String label;
	/** the associated field **/
	protected Field field;	
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
		if(label != null && !label.equals("")){
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
	
	public abstract boolean hasChanged();
	
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
	
	/**
	 * should set the response of the thinlet field, in it's designated format
	 * @param response
	 */
	public abstract void setRawResponse(E response);

	/**
	 * @return The field that this ThinletFormField is linked to
	 */
	public Field getField(){
		return field;
	}
	
	/**
	 * Links this ThinletFormField to a field
	 * @param field
	 */
	public void setField(Field field){
		this.field = field;
	}
	
	/**
	 * Sets this field editable based on the boolean parameter
	 * @param editable
	 */
	public void setEditable(boolean editable){
		thinlet.setEditableRecursively(getThinletPanel(), editable);
	}
	
	/**
	 * Sets all components of this field editable based on the boolean parameter
	 * @param editable
	 */
	public void setEnabled(boolean editable){
		thinlet.setEnabledRecursively(getThinletPanel(), editable);
	}
		
	/**
	 * Should return the ThinletFormField that corresponds to the datatype provided
	 * Should also figure out a better way to do this
	 * @param datatype
	 * @param uiController
	 * @param label
	 * @return The ThinletFormField
	 */
	public static ThinletFormField getThinletFormFieldForDataType(DataType datatype, UiGeneratorController uiController, String label){
		if(datatype == DataType.CHECK_BOX){ 
			return new CheckBox(uiController,label);
		}else if(datatype ==  DataType.DATE_FIELD){
			return new DateField(uiController,label);
		}else if(datatype == DataType.NUMERIC_TEXT_FIELD){
			return new NumericTextField(uiController,label);
		}else if(datatype == DataType.PASSWORD_FIELD){
			return new PasswordTextField(uiController,label);
		}else if(datatype == DataType.PHONE_NUMBER_FIELD){
			return new PhoneNumberField(uiController,label);
		}else if(datatype == DataType.TIME_FIELD){
			return new TimeField(uiController,label);
		}else if(datatype == DataType.TEXT_AREA){
			return new TextArea(uiController,label);
		}else if(datatype == DataType.TEXT_FIELD){
			return new TextBox(uiController,label);
		}else if(datatype == DataType.POSITIVENEGATIVE){
			return new ButtonGroup(uiController,label,getI18NString("datatype.positive"),getI18NString("datatype.negative"));
		}else if(datatype == DataType.TRUEFALSE){
			return new ButtonGroup(uiController,label,getI18NString("datatype.true"),getI18NString("datatype.false"));
		}else if(datatype == DataType.YESNO){
			return new ButtonGroup(uiController,label,getI18NString("datatype.yes"),getI18NString("datatype.no"));
		}
		return null;
	}
}
