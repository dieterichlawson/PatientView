package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.Field;

/**
 * The FieldDao is used for retrieving field objects from the database. There
 * are no save, update, or delete methods because Field is an abstract class and
 * should never be directly instantiated or persisted. The DAOS for the
 * subclasses of Field provide this functionality.
 */
public interface FieldDao {

	/**
	 * Returns the field with the specified ID. If there is no such field, null
	 * is returned
	 * 
	 * @param id
	 * @return
	 */
	public Field getFieldById(long id);

	/**
	 * Returns all fields in the system
	 * 
	 * @return
	 */
	public List<Field> getAllFields();

	/**
	 * Returns all fields with labelFragment anywhere in the label.
	 * 
	 * @param label
	 * @return
	 */
	public List<Field> findFieldsByLabel(String labelFragment);

	/**
	 * Returns all fields with exactly the label supplied
	 * 
	 * @param label
	 * @return
	 */
	public List<Field> getFieldsByLabel(String label);
}
