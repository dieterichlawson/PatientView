package net.frontlinesms.plugins.patientview.ui.dialogs.searchareas;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernatePatientDao;
import net.frontlinesms.plugins.patientview.ui.dialogs.SubmitFormDialog;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

public class PatientSearchArea extends EntitySearchArea<Patient>{

	private SubmitFormDialog fofDialog;
	private HibernatePatientDao patientDao;
	private CommunityHealthWorker chw;

	public PatientSearchArea(Patient entity, ExtendedThinlet uiController,SubmitFormDialog fofDialog,ApplicationContext appCon) {
		super(entity, uiController);
		this.fofDialog= fofDialog;
		patientDao = (HibernatePatientDao) appCon.getBean("PatientDao");
	}

	@Override
	protected Collection<Patient> getEntitiesForString(String s) {
		if(chw == null){
			return patientDao.getPatientsByNameWithLimit(s,20);
		}else{
			return patientDao.getPatientsByCHWAndName(s, chw);
		}
	}
	
	public void setCommunityHealthWorker(CommunityHealthWorker chw){
		this.chw = chw;
	}

	@Override
	protected String getEntityName(Patient entity) {
		return entity.getName();
	}

	@Override
	protected String getEntityTypeName() {
		return "Patient";
	}

	@Override
	public void selectionChanged() {
		Patient patient = (Patient) uiController.getAttachedObject(uiController.getSelectedItem(table));
		fofDialog.setPatient(patient);
	}
	
	@Override
	protected Object setUpTable(){
		Object header = uiController.create("header");
		uiController.add(header,uiController.createColumn("Patient",null));
		uiController.add(header,uiController.createColumn("Patient's CHW",null));
		uiController.removeAll(table);
		uiController.add(table,header);
		return table;
	}
	
	@Override
	protected void setTableResults(Collection<Patient> entities){
		for(Patient entity:entities){
			Object row = uiController.createTableRow(entity);
			Object cell = uiController.createTableCell(entity.getName());
			Object cell1= uiController.createTableCell(entity.getChw().getName());
			uiController.add(row,cell);
			uiController.add(row,cell1);
			uiController.add(table,row);
		}
	}

}