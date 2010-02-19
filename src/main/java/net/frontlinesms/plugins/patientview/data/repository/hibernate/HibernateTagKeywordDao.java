package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.response.TagKeyword;

public class HibernateTagKeywordDao extends BaseHibernateDao<TagKeyword>{

	protected HibernateTagKeywordDao() {
		super(TagKeyword.class);
	}

	public void deleteTagKeyword(TagKeyword t) {
		super.delete(t);
	}

	public Collection<TagKeyword> getAllTagKeywords() {
		return super.getAll();
	}

	public void saveTagKeyword(TagKeyword t) {
		super.saveWithoutDuplicateHandling(t);
	}

	public void updateTagKeyword(TagKeyword t) {
		super.updateWithoutDuplicateHandling(t);
	}
	
}
