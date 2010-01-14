package net.frontlinesms.plugins.medic.data.domain.response;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="medic_tag_keywords")
public class TagKeyword {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long kid;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER)
	@JoinColumn(name="tag")
	private Tag tag;
	
	private String keyword;

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public TagKeyword(Tag tag, String keyword) {
		super();
		this.tag = tag;
		this.keyword = keyword;
	}
	
}
