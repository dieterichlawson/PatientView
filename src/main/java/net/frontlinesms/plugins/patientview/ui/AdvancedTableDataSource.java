package net.frontlinesms.plugins.patientview.ui;

/**
 * An interface that the source of data for an Advanced Table Controller should implement
 * @author Dieterich
 *
 */
public interface AdvancedTableDataSource {
	
	/**
	 * Called when the advanced table controller is notified of a change to the database.
	 * The data source can update its results to reflect those changes if it so chooses. 
	 */
	public void refreshResults();
}
