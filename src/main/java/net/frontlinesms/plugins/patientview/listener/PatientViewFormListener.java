package net.frontlinesms.plugins.patientview.listener;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.DummyDataGenerator;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
public class PatientViewFormListener implements EventObserver{
	
	private MedicFormDao formDao;
	private UserDao userDao;
	private boolean listening = true;
	private static Logger LOG = FrontlineUtils.getLogger(PatientViewFormListener.class);
	
	public PatientViewFormListener(ApplicationContext appCon){
		this.formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		this.userDao = (UserDao) appCon.getBean("UserDao");
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
	}

	public void notify(FrontlineEventNotification notification) {
		if(!listening){
			return;
		}
		if(notification instanceof EntityUpdatedNotification){
			if(((EntityUpdatedNotification) notification).getDatabaseEntity() instanceof Form){
				Form f =  ((EntityUpdatedNotification<Form>) notification).getDatabaseEntity();
				if(f.isFinalised() && formDao.getMedicFormForForm(f) == null && userDao.getAllUsers().size() > 0){
					LOG.trace("Attempting to create Medic Form from FrontlineSMS Form");
					try{
						MedicForm newForm = new MedicForm(f);
						for(MedicFormField mff : newForm.getFields()){
							if(mff.getLabel().equalsIgnoreCase(getI18NString("medic.field.mapping.birthdate")) && mff.getDatatype() == DataType.DATE_FIELD){
								mff.setMapping(PatientFieldMapping.BIRTHDATEFIELD);
								LOG.trace("Mapped field \"" + mff.getLabel()+"\" as a birthday field");
							}else if(mff.getLabel().equalsIgnoreCase(getI18NString("medic.field.mapping.name")) && mff.getDatatype() == DataType.TEXT_FIELD){
								mff.setMapping(PatientFieldMapping.NAMEFIELD);
								LOG.trace("Mapped field \"" + mff.getLabel()+"\" as a patient name field");
							}else if(mff.getLabel().equalsIgnoreCase(getI18NString("medic.field.mapping.id")) && (mff.getDatatype() == DataType.TEXT_FIELD || mff.getDatatype() == DataType.NUMERIC_TEXT_FIELD)){
								mff.setMapping(PatientFieldMapping.IDFIELD);
								LOG.trace("Mapped field \"" + mff.getLabel()+"\" as a patient ID field");
							}
					}
					formDao.saveMedicForm(newForm);
					}catch(Throwable e){
						LOG.error("Unable to create Medic Form from FrontlineSMS form",e);
					}
				}
			}
		}
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public boolean isListening() {
		return listening;
	}
}
