package net.frontlinesms.plugins.patientview.ui.detailview;

import java.util.HashMap;

/**
 * An interface that all Detail View panels should implement
 * It provides structure so that the proper panel can be placed in the Detail View
 * when the related entity is selected in the main search screen.
 * 
 * Additionally, the panel will be notified before it appears and disappears so that it
 * can do any loading/unloading necessary
 * @author Dieterich
 *
 * @param <E> The class that this panel is linked to (This panel will be shown when this class is selected).
 */
public interface DetailViewPanelController<E> {

	/**
	 * @return The class that this panel is linked to
	 */
	public Class getEntityClass();
	
	/**
	 * Should return the main panel, which will be added into a panel with 1 column
	 * @return
	 */
	public Object getPanel();
	
	/**
	 * Method that notifies the controller before it appears
	 * @param entity The entity that has been selected
	 */
	public void viewWillAppear(E entity);
	
	/**
	 * Notifies the object just before it disappears
	 */
	public void viewWillDisappear();
	
	/**
	 * Return a String/String map that represents button titles and button method calls.
	 * These buttons will be placed in the Further Options box below the detail view
	 * panel
	 * @return
	 */
	public HashMap<String,String> getFurtherOptions();
}
