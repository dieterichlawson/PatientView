package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.HistoryEntry;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.HistoryEntryDao;

import org.hibernate.Query;

public class HibernateHistoryEntryDao extends BaseHibernateDao<HistoryEntry>
									  implements HistoryEntryDao {

	private static String entryByActorQuery = "select entry from HistoryEntry entry where entry.actor = ";
	private static String entryBySubjectQuery = "select entry from HistoryEntry entry where entry.subject = ";
	
	protected HibernateHistoryEntryDao() {
		super(HistoryEntry.class);
	}

	public void deleteHistoryEntry(HistoryEntry entry) {
		super.delete(entry);
	}

	public Collection<HistoryEntry> getAllHistoryEntrys() {
		return super.getAll();
	}

	public List<HistoryEntry> getHistoryEntriesForActor(Person p) {
		Query q = super.getSession().createQuery(entryByActorQuery + p.getPid());
		return q.list();
	}

	public List<HistoryEntry> getHistoryEntriesForSubject(Person p) {
		Query q = super.getSession().createQuery(entryBySubjectQuery + p.getPid());
		return q.list();
	}

	public void saveHistoryEntry(HistoryEntry entry) {
		super.saveWithoutDuplicateHandling(entry);
	}

}
