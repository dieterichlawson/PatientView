package net.frontlinesms.plugins.patientview.data.domain.response;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="medic_tags")
public class Tag {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long tid;
	
	private String tag;
	
	@ManyToMany(cascade={},fetch=FetchType.LAZY)
	@JoinTable(name="tag_map",
			joinColumns= @JoinColumn(name="response"),
			inverseJoinColumns=@JoinColumn(name="tag"))
	private List<Response> content;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	@JoinColumn(name="parent_tag")
	private Tag parentTag;
	
	@OneToMany(cascade={},mappedBy="parentTag")
	private List<Tag> childTags;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="tag")
	private List<TagKeyword> keywords;
	
	public Tag(String tag) {
		super();
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Tag getParentTag() {
		return parentTag;
	}
	
	public void setParentTag(Tag parentTag) {
		this.parentTag = parentTag;
	}
	
	public List<Tag> getChildTags() {
		return childTags;
	}
	
	public void setChildTags(List<Tag> childTags) {
		this.childTags = childTags;
	}
	
	public void addChildTag(Tag t){
		t.setParentTag(this);
		childTags.add(t);
	}
	
	public void removeTag(Tag t){
		childTags.remove(t);
	}
	
	public long getTid() {
		return tid;
	}
	
	public void addKeyword(TagKeyword tk){
		keywords.add(tk);
	}
	
	public void removeKeyword(TagKeyword tk){
		keywords.remove(tk);
	}
}
