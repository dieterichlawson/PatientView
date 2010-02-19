package net.frontlinesms.plugins.patientview.ui.dialogs.searchareas;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormDao;
import net.frontlinesms.plugins.patientview.ui.dialogs.SubmitFormDialog;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

public class FormSearchArea extends EntitySearchArea<MedicForm>{
	
	private HibernateMedicFormDao formDao;

	private SubmitFormDialog fofDialog;
	
	public FormSearchArea(MedicForm entity, ExtendedThinlet uiController, SubmitFormDialog fofDialog, ApplicationContext appCon) {
		super(entity, uiController);
		this.fofDialog = fofDialog;
		formDao = (HibernateMedicFormDao) appCon.getBean("MedicFormDao");
		searchBarKeyPressed("");
	}

	
	@Override
	protected Collection<MedicForm> getEntitiesForString(String s) {
		return formDao.getFormsForString(s);
	}

	@Override
	protected String getEntityName(MedicForm entity) {
		return entity.getName();
	}

	@Override
	protected String getEntityTypeName() {
		return "Form";
	}

	@Override
	public void selectionChanged() {
		MedicForm form = (MedicForm) uiController.getAttachedObject(uiController.getSelectedItem(table));
		fofDialog.setForm(form);
	}
	
	

}
