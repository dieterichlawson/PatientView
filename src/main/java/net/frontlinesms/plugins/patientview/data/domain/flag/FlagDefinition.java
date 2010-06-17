package net.frontlinesms.plugins.patientview.data.domain.flag;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="medic_flag_definitions")
public class FlagDefinition {

		/** Unique id for this entity.  This is for hibernate usage. */
		@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
		@Column(unique=true,nullable=false,updatable=false)
		private long fid;
		
		private String name;
		
		private String iconPath;
		
		private String shortDescription;
		
		private String flagCondition;
		
		@OneToMany(fetch=FetchType.LAZY,mappedBy="flag",targetEntity=PatientFlag.class)
		private Set<PatientFlag> flags;
		
		public FlagDefinition(String name, String shortDescription, String flagCondition) {
			super();
			this.name = name;
			this.shortDescription = shortDescription;
			this.flagCondition = flagCondition;
		}
		
		protected FlagDefinition(){}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setShortDescription(String shortDescription) {
			this.shortDescription = shortDescription;
		}

		public String getShortDescription() {
			return shortDescription;
		}

		public void setFlagCondition(String flagCondition) {
			this.flagCondition = flagCondition;
		}

		public String getFlagCondition() {
			return flagCondition;
		}

		public long getFid() {
			return fid;
		}

		public void setIconPath(String iconPath) {
			this.iconPath = iconPath;
		}

		public String getIconPath() {
			return iconPath;
		}
		
		
}
