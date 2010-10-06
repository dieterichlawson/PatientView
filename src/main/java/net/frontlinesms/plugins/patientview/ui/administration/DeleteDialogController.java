package net.frontlinesms.plugins.patientview.ui.administration;

import thinlet.Thinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * A controller class for showing 'delete' options to the user
 * @author dieterichlawson
 *
 */
public class DeleteDialogController implements ThinletUiEventHandler {

	private UiGeneratorController uiController;
	
	private final static String UI_XML = "/ui/plugins/patientview/administration/deleteDialog.xml";
	
	/**
	 * The parent controller that will be notified when this dialog closes
	 */
	private DeleteDialogDelegate parentController;
	
	/** Top level thinlet object*/
	private Object dialog;
	private Object keepVisibleCheckbox;
	private Object reasonTextArea;
	
	/** The name of the entity, e.g. Patient, CHW, etc..*/
	private String entityName;
	
	public DeleteDialogController(UiGeneratorController uiController, DeleteDialogDelegate parentController, String entityName){
		this.uiController = uiController;
		this.parentController = parentController;
		this.entityName = entityName;
		init();
	}
	
	private void init(){
		dialog = uiController.loadComponentFromFile(UI_XML, this);
		keepVisibleCheckbox = uiController.find(dialog,"keepVisibleCheckbox");
		reasonTextArea  = uiController.find(dialog,"reasonArea");
		uiController.setText(dialog, "Delete " + entityName);
		uiController.setText(keepVisibleCheckbox, "Keep " + entityName + " visible (in search results, etc..)");
		uiController.add(dialog);
		uiController.setVisible(dialog, true);
	}
	
	public void deleteClicked(){
		closeDialog();
		parentController.dialogReturned(true, uiController.getBoolean(keepVisibleCheckbox, Thinlet.SELECTED), uiController.getText(reasonTextArea));
	}
	
	public void cancelClicked(){
		closeDialog();
		parentController.dialogReturned(false,null,null);
	}

	private void closeDialog(){
		uiController.setVisible(dialog,false);
		uiController.remove(dialog);
	}
}
