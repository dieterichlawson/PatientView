package net.frontlinesms.plugins.patientview.ui.personpanel;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.ui.imagechooser.ImageChooser;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.PersonFieldGroup;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

public abstract class PersonPanel<E extends Person> implements ThinletUiEventHandler {
	
	//Controller objects
	protected UiGeneratorController uiController;
	protected ApplicationContext appCon;
	
	/** The person this panel is for */
	private E person;
	
	/**The controller for the group of fields used when editing the person*/
	private PersonFieldGroup<E> fieldGroup;
	
	/** The delegate will recieve notifications from this PersonPanel, if set*/
	protected PersonPanelDelegate delegate;

	/** true if the panel is editing the patient*/
	protected boolean inEditingMode;
	
	/** true if the panel is for creating a new person */
	protected boolean isNewPersonPanel;
	
	//thinlet objects
	private Object mainPanelContainer;
	protected Object mainPanel;

	private static String PERSON_PANEL_XML = "/ui/plugins/patientview/components/personPanel.xml";
	
	// i18n constants
	private static final String BDAY_LABEL = "thinletformfields.birthdate";
	private static final String ID_LABEL = "medic.common.labels.id";
	
	public static final String BUTTON_PANEL = "personpanelbuttons";
	
	/**
	 * The general constructor that creates a panel for person p. If person p is
	 * null, it creates an 'add person' panel
	 * 
	 * @param uiController
	 * @param p
	 */
	 PersonPanel(UiGeneratorController uiController, ApplicationContext appCon, E p) {
		this.uiController = uiController;
		this.appCon = appCon;
		this.mainPanelContainer = Thinlet.create("panel");
		uiController.setColumns(mainPanelContainer, 1);
		uiController.setInteger(mainPanelContainer, "weightx", 1);
		if (p != null) {
			isNewPersonPanel = false;
			inEditingMode = false;
			this.person = p;
			addNonEditableFields();
		} else {
			isNewPersonPanel = true;
			inEditingMode = true;
			addEditableFields();
		}
	}

	/**
	 * A constructor for creating person panels that are meant to add new people
	 * to the system. This constructor includes a delegate for callbacks
	 * 
	 * @param uiController
	 *            the UI controller
	 */
	public PersonPanel(UiGeneratorController uiController, ApplicationContext appCon, PersonPanelDelegate delegate) {
		this.uiController = uiController;
		this.appCon = appCon;
		this.mainPanelContainer = Thinlet.create("panel");
		this.delegate = delegate;
		isNewPersonPanel = true;
		inEditingMode = true;
		uiController.setInteger(mainPanelContainer, "weightx", 1);
		addEditableFields();
	}

	/**
	 * A constructor for creating person panels that are meant to add new people
	 * to the system
	 * 
	 * @param uiController
	 *            the UI controller
	 */
	public PersonPanel(UiGeneratorController uiController, ApplicationContext appCon) {
		this.uiController = uiController;
		this.appCon = appCon;
		this.mainPanelContainer = Thinlet.create("panel");
		isNewPersonPanel = true;
		inEditingMode = true;
		uiController.setInteger(mainPanelContainer, "weightx", 1);
		addEditableFields();
	}

	protected abstract void addAdditionalDemoFields();

	/**
	 * Should create a person of whichever person type the implementing class is
	 * for. Should do this by instantiating the "person" field
	 */
	protected abstract E createPerson();

	/**
	 * @return The title for the Person Panel
	 */
	protected abstract String getDefaultTitle();

	/**
	 * @return The title for the person panel while editing
	 */
	protected abstract String getEditingTitle();

	/**
	 * @return The title for the person panel while adding
	 */
	protected abstract String getAddingTitle();

	/**
	 * Should perform additional setup after the core fields are added to the
	 * panel
	 * 
	 * @param labelPanel
	 */
	protected abstract void addAdditionalFields();

	/**
	 * Should perform additional setup with editable fields after the core
	 * fields are added
	 */
	protected abstract PersonFieldGroup<E> getEditableFields();

	/**
	 * Method that is called when the picture is clicked If the panel is in
	 * editing mode, then an add picture dialog is brought up Otherwise, the
	 * picture is just displayed in a bigger size and a separate frame
	 */
	@SuppressWarnings("serial")
	public void imageClicked() {
		if (inEditingMode) {
			ImageChooser chooser = new ImageChooser();
			if (chooser.getImage() != null) {
				if (getPerson() == null) {
					createPerson();
				}
				getPerson().setImage(chooser.getImage(), chooser.getExtension());
				uiController.setIcon(uiController.find(mainPanel, "imagePanel"), getPerson().getResizedImage());
			}
		} else if (getPerson().hasImage()) {
			Thinlet thinlet = new Thinlet();
			Object panel = Thinlet.create("panel");
			BufferedImage image = getPerson().getImage();
			int width = 650;
			int height = 650;
			width = Math.min(width, image.getWidth());
			height = Math.min(height, image.getHeight());
			thinlet.setIcon(panel, "icon", getPerson().getImage());
			thinlet.add(panel);
			FrameLauncher f = new FrameLauncher(getPerson().getName(), thinlet, width + 10, height + 10, null) {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			};
		}
	}

	/**
	 * creates a new panel with non-editable fields for the person that this
	 * class was initialized with This method adds the first four fields: name,
	 * id, gender, and age, and then calls the abstract method
	 * addAdditionalFields, which should be implemented to do additional
	 * displaying of information in subclasses
	 */
	private void addNonEditableFields() {
		uiController.removeAll(mainPanelContainer);
		inEditingMode = false;
		mainPanel = uiController.loadComponentFromFile(PERSON_PANEL_XML, this);
		uiController.setAction(uiController.find(mainPanel, "imagePanel"),
				"imageClicked()", null, this);
		if (getPerson().hasImage()) {
			uiController.setIcon(uiController.find(mainPanel, "imagePanel"),
					getPerson().getResizedImage());
		}else{
			uiController.setIcon(uiController.find(mainPanel, "imagePanel"), person.getGender() == Gender.MALE? "/icons/male_outline.png":"/icons/female_outline.png");
			uiController.setEnabled(uiController.find(mainPanel, "imagePanel"), false);
		}
		// add the core fields
		addLabelToLabelPanel(getPerson().getName());
		addLabelToLabelPanel(getI18NString(ID_LABEL) + ": " + getPerson().getPid());
		addLabelToLabelPanel(getPerson().getGender().toString());
		addLabelToLabelPanel(getI18NString(BDAY_LABEL) + ": " + getPerson().getStringBirthdate());
		// let the subclasses add additional fields
		addAdditionalFields();
		uiController.add(mainPanelContainer, mainPanel);
		uiController.setText(mainPanel, getDefaultTitle());
	}

	/**
	 * Replaces all labels in the person panel with editable controls for
	 * modifying the person's data
	 */
	private void addEditableFields() {
		uiController.removeAll(mainPanelContainer);
		mainPanel = uiController.loadComponentFromFile(PERSON_PANEL_XML, this);
		uiController.setAction(uiController.find(mainPanel, "imagePanel"),"imageClicked()", null, this);
		// set the edit image button
		if(person != null){
			uiController.setIcon(uiController.find(mainPanel, "imagePanel"), person.getGender() == Gender.MALE? "/icons/male_outline_add.png":"/icons/female_outline_add.png");
		}else{
			uiController.setIcon(uiController.find(mainPanel, "imagePanel"), "/icons/female_outline_add.png");
		}
		uiController.setEnabled(uiController.find(mainPanel,"imagePanel"), true);
		// get the panel with all the labels in it and remove everything
		Object labelPanel = getLabelPanel();
		uiController.removeAll(labelPanel);
		//add all the editable fields
		this.fieldGroup = getEditableFields();
		uiController.add(labelPanel,fieldGroup.getMainPanel());
		inEditingMode = true;
		if (isNewPersonPanel) {
			uiController.setText(mainPanel, getAddingTitle());
		} else {
			uiController.setText(mainPanel, getEditingTitle());
		}
		uiController.add(getLabelPanel(), getSaveCancelButtons());
		uiController.add(mainPanelContainer, mainPanel);
	}

	/**
	 * Updates the person to reflect all the responses to the fields and then
	 * writes the changes to the database
	 */
	private boolean validateAndSaveFieldResponses() {
		boolean isValid = fieldGroup.saveIfValid(true);
		if(isNewPersonPanel && isValid){
			delegate.didCreatePerson();
		}
		return isValid;       
	}

	/**
	 * @return a Thinlet panel with 2 buttons inside of it for saving and
	 *         cancelling editing
	 */
	private Object getSaveCancelButtons() {
		Object saveCancelPanel = Thinlet.create("panel");
		uiController.setName(saveCancelPanel, BUTTON_PANEL);
		uiController.setInteger(saveCancelPanel, "gap", 10);
		uiController.setInteger(saveCancelPanel, "right", 10);
		uiController.setChoice(saveCancelPanel, "halign", "fill");
		uiController.setWeight(saveCancelPanel, 1, 1);
		Object saveButton = uiController.createButton(getI18NString("detailview.buttons.save"));
		uiController.setName(saveButton,"savebutton");
		uiController.setAction(saveButton, "stopEditingWithSave()", null, this);
		uiController.setChoice(saveButton, "halign", "left");
		uiController.setIcon(saveButton, "/icons/tick.png");
		Object cancelButton = uiController.createButton(getI18NString("detailview.buttons.cancel"));
		uiController.setAction(cancelButton, "stopEditingWithoutSave()", null, this);
		uiController.setChoice(cancelButton, "halign", "right");
		uiController.setIcon(cancelButton, "/icons/cross.png");
		uiController.add(saveCancelPanel, saveButton);
		if (delegate == null) {
			uiController.add(saveCancelPanel, cancelButton);
		}
		return saveCancelPanel;
	}

	public Object getLabelPanel() {
		return uiController.find(mainPanel, "labelPanel");
	}

	/**
	 * Switches the mode to editing mode, where the user can edit the person's
	 * core info
	 */
	public void switchToEditingPanel() {
		addEditableFields();
	}

	/**
	 * Switches from editing mode back to normal mode, saving any changes that
	 * have occurred
	 */
	public void stopEditingWithSave() {
		if (validateAndSaveFieldResponses()) {
			addNonEditableFields();
		}
	}

	/**
	 * Switches from editing mode back to normal mode, without saving any
	 * changes
	 */
	public void stopEditingWithoutSave() {
		if (isNewPersonPanel) {
			uiController.removeAll(mainPanelContainer);
			return;
		}
		addNonEditableFields();
	}

	public Object getMainPanel() {
		return mainPanelContainer;
	}

	/**
	 * Used to add descriptive labels to the space next to the picture. Used to
	 * add name, gender, phone number, etc..
	 * 
	 * @param text
	 *            The text for the label
	 */
	protected void addLabelToLabelPanel(String text) {
		Object label = uiController.createLabel(text);
		uiController.setInteger(label, "weightx", 1);
		uiController.setInteger(label, "weighty", 1);
		uiController.add(getLabelPanel(), label);
	}

	protected void setNameLabel(String name) {
		uiController.setText(uiController.find(mainPanel, "nameLabel"), name);
	}

	/**
	 * Sets the title of the PersonPanel
	 * @param title
	 */
	public void setPanelTitle(String title) {
		uiController.setText(mainPanel, title);
	}
	
	/**
	 * @return The person that this PersonPanel is for
	 */
	public E getPerson() {
		return person;
	}

}
