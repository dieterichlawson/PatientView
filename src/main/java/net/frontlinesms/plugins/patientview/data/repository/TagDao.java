package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.response.Tag;

public interface TagDao {
	
	public void saveTag(Tag s);

	public void deleteTag(Tag s);
	
	public void updateTag(Tag s);
	
	public Tag getTagNamed(String s);
	
	public Collection<Tag> getTagsForKeyword(String s);
	
	public Collection<Tag> getAllTags();
}
