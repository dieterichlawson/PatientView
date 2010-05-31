package net.frontlinesms.plugins.patientview;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;

import org.springframework.context.ApplicationContext;
public class PatientViewFormListener implements EventObserver{
	
	private MedicFormDao formDao;
	
	public PatientViewFormListener(ApplicationContext appCon){
		this.formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
	}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntityUpdatedNotification){
			if(((EntityUpdatedNotification) notification).getDatabaseEntity() instanceof Form){
				Form f =  ((EntityUpdatedNotification<Form>) notification).getDatabaseEntity();
				if(f.isFinalised() && formDao.getMedicFormForForm(f) == null){
					MedicForm newForm = new MedicForm(f);
					for(MedicFormField mff : newForm.getFields()){
						if(mff.getLabel().equalsIgnoreCase(getI18NString("medic.field.mapping.birthdate"))){
							mff.setMapping(PatientFieldMapping.BIRTHDATEFIELD);
						}else if(mff.getLabel().equalsIgnoreCase(getI18NString("medic.field.mapping.name"))){
							mff.setMapping(PatientFieldMapping.NAMEFIELD);
						}else if(mff.getLabel().equalsIgnoreCase(getI18NString("medic.field.mapping.id"))){
							mff.setMapping(PatientFieldMapping.IDFIELD);
						}
					}
					formDao.saveMedicForm(newForm);
				}
			}
		}
	}
}
