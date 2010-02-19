package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.framework.HistoryEntry;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

public interface HistoryEntryDao {

	/**
	 * Saves a History Entry to the data source
	 * @param entry the History Entry to save
	 */
	public void saveHistoryEntry(HistoryEntry entry);
	
	/**
	 * Deletes a History Entry from the data source.
	 * @param entry History Entry to save
	 */
	public void deleteHistoryEntry(HistoryEntry entry);
	
	/** @return all History Entries saved in the data source */
	public Collection<HistoryEntry> getAllHistoryEntrys();
	
	public Collection<HistoryEntry> getHistoryEntriesForSubject(Person p);
	
	public Collection<HistoryEntry> getHistoryEntriesForActor(Person p);
	
}
