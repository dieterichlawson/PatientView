package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.response.Tag;
import net.frontlinesms.plugins.patientview.data.domain.response.TagKeyword;

public class HibernateTagDao extends BaseHibernateDao<Tag>{

	protected HibernateTagDao() {
		super(Tag.class);
	}

	public void deleteTag(Tag t) {
		super.delete(t);
	}

	public Collection<Tag> getAllTags() {
		return super.getAll();
	}

	public void saveTag(Tag t) {
		super.saveWithoutDuplicateHandling(t);
	}

	public void updateTag(Tag t) {
		super.updateWithoutDuplicateHandling(t);
	}
	
	public void addKeywordToTag(TagKeyword k, Tag t){
		t.addKeyword(k);
		updateTag(t);
	}
}

