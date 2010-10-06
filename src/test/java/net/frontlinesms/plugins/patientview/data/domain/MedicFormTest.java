package net.frontlinesms.plugins.patientview.data.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;

/**
 * Tests the MedicForm domain object.
 * This is necessary because MedicForm
 * has logic for maintaining the positions of fields
 * on the form and for creating MedicForms from vanilla FrontlinesSMS
 * forms
 */
public class MedicFormTest extends BaseTestCase {

	private static final Random random = new Random();

	/**
	 * A sample form name
	 */
	private static final String FORM_NAME = "A Form";

	/**
	 * Tests if when you call the constructor that accepts a string
	 * for the name that the fields arraylist is initialized
	 */
	public void test_nameConstructor_invoked_fieldsNotNull() {
		MedicForm form = new MedicForm(FORM_NAME);
		assertNotNull(form.getFields());
		System.out.println("IM BEING CALLED!");
	}

	/**
	 * Tests if when you pass setFields 10 fields that their positions are properly set
	 */
	public void test_setFormFields_passedTenFields_fieldPositionsInOrder() {
		MedicForm form = new MedicForm(FORM_NAME);
		form.setFields(createMockFormFieldList(10, false));
		assertFieldPositionsInOrder(form);
	}

	/**
	 * Tests if when you pass setFields 1 field that its position is properly set
	 */
	public void test_setFormFields_passedOneField_fieldPositionsInOrder() {
		MedicForm form = new MedicForm(FORM_NAME);
		form.setFields(createMockFormFieldList(1, false));
		assertFieldPositionsInOrder(form);
	}

	/**
	 * Tests whether setFields can handle a null value
	 */
	public void test_setFormFields_passedNullList_fieldsNull() {
		MedicForm form = new MedicForm(FORM_NAME);
		form.setFields(null);
		assertNull(form.getFields());
	}

	/**
	 * Tests if setFields sets each field's parent form 
	 */
	public void test_setFormFields_passedTenFields_fieldsHaveCorrectForm() {
		MedicForm form = new MedicForm(FORM_NAME);
		form.setFields(createMockFormFieldList(10, false));
		assertFieldsParentFormProperlySet(form);
	}
	
	/**
	 * Tests if addField correctly updates the new field's position
	 */
	public void test_addFormField_passedField_positionCorrect(){
		MedicForm form = createMockForm(10,true);
		form.addField(createMockFormField());
		assertFieldPositionsInOrder(form);
	}
	
	/**
	 * tests if addField correctly sets the field's parent form
	 */
	public void test_addFormField_passedField_fieldParentFormCorrect(){
		MedicForm form = createMockForm(10,true);
		form.addField(createMockFormField());
		assertFieldsParentFormProperlySet(form);
	}
	
	/**
	 * tests if addField adds the field to the end of the field list
	 */
	public void test_addFormField_passedField_fieldAddedToEnd(){
		MedicForm form = createMockForm(10,true);
		MedicFormField mff = createMockFormField();
		form.addField(mff);
		assertEquals(mff,form.getFields().get(form.getFields().size() -1));
	}
	
	/**
	 * tests if the field positions are correct after removing a field
	 */
	public void test_removeFormField_passedField_positionCorrect(){
		MedicForm form = createMockForm(10,true);
		form.removeField(form.getFields().get(3));
		assertFieldPositionsInOrder(form);
	}
	
	/**
	 * Tests if a form is named correctly after you create it from a Vanilla FrontlineSMS Form
	 */
	public void test_vanillaFormConstructor_invoked_formNamedCorrectly(){
		Form vanillaForm = new Form(FORM_NAME);
		MedicForm form = new MedicForm(vanillaForm);
		assertEquals(vanillaForm.getName(),form.getName());
	}
	
	/**
	 * Tests if the fields are created correctly if you create a medic form from a Vanilla Frontline form
	 */
	public void test_vanillaFormConstructor_invoked_fieldsCreatedCorrectly(){
		Form vanillaForm = new Form(FORM_NAME);
		for(FormFieldType fft: FormFieldType.values()){
			FormField vanillaField = new FormField(fft, fft.toString()+ " Field");
			vanillaForm.addField(vanillaField);
		}
		MedicForm form = new MedicForm(vanillaForm);
		for(int i = 0; i < form.getFields().size(); i++){
			//datatype is correct
			assertEquals(vanillaForm.getFields().get(i).getType().toString(),form.getFields().get(i).getDatatype().name());
			//name is correct
			assertEquals(vanillaForm.getFields().get(i).getLabel(),form.getFields().get(i).getLabel());
		}
		//position is correct
		assertFieldPositionsInOrder(form);
		//form is correct
		assertFieldsParentFormProperlySet(form);
	}

	/**
	 * Creates a mock medic form
	 * @param fieldNum the number of fields to include
	 * @param ordered true if you want the positions to be correct
	 * @return the mock form
	 */
	private MedicForm createMockForm(int fieldNum, boolean ordered){
		MedicForm mf = new MedicForm();
		mf.setName(FORM_NAME);
		mf.setFields(createMockFormFieldList(fieldNum, ordered));
		return mf;
	}
	/**
	 * Creates a list of mock form fields of the specified length. If unordered
	 * is true, it assigns each field a random position from 0 to 100.
	 * Otherwise, the fields are assigned the proper position.
	 * 
	 * @param length
	 * @param ordered
	 * @return
	 */
	private List<MedicFormField> createMockFormFieldList(int length, boolean ordered) {
		List<MedicFormField> fields = new ArrayList<MedicFormField>();
		for (int i = 0; i < length; i++) {
			MedicFormField field = createMockFormField();
			field.setLabel(field.getLabel() + " " + i);
			if (ordered) {
				field.setPosition(i);
			} else {
				field.setPosition(random.nextInt(100));
			}
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Creates a mock form field with a random data type and a name 
	 * corresponding to that data type
	 * @return
	 */
	private MedicFormField createMockFormField() {
		DataType type = DataType.values()[random.nextInt(DataType.values().length)];
		return new MedicFormField(null, type, type.name() + " Field");
	}

	/**
	 * Iterates through all fields on a form and asserts
	 * that their position equals their position in the list
	 * @param form
	 */
	private void assertFieldPositionsInOrder(MedicForm form) {
		for (int i = 0; i < form.getFields().size(); i++) {
			assertEquals(i,form.getFields().get(i).getPosition());
		}
	}
	
	/**
	 * Iterates through all fields on a form and asserts
	 * that the fields' parent form is set properly
	 * @param form
	 */
	private void assertFieldsParentFormProperlySet(MedicForm form) {
		for (int i = 0; i < form.getFields().size(); i++) {
			assertEquals(form, form.getFields().get(i).getForm());
		}
	}
}
