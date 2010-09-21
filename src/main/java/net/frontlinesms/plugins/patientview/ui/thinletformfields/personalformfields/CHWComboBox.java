package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateCommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class CHWComboBox extends ThinletFormField<CommunityHealthWorker> implements PersonalFormField{
	
	private CommunityHealthWorker response;
	private Object comboBox;
	private HibernateCommunityHealthWorkerDao chwDao;
	private boolean hasChanged = false;
	
	public CHWComboBox(ExtendedThinlet thinlet, ApplicationContext appCon, CommunityHealthWorker chw, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18NString("medic.common.chw")+":", delegate);
		comboBox =ExtendedThinlet.create("combobox");
		thinlet.setInsert(comboBox,"textChanged(this.text)", null, this);
		thinlet.setRemove(comboBox, "textChanged(this.text)", null, this);
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		chwDao = (HibernateCommunityHealthWorkerDao) appCon.getBean("CHWDao");
		if(chw != null){
			thinlet.setText(comboBox, chw.getName());
			response = chw;
			textChanged(chw.getName());
		}
		thinlet.add(mainPanel,comboBox);
		thinlet.setWeight(comboBox, 5, 0);
		thinlet.setColspan(mainPanel, 2);
		textChanged("");
	}

	public void textChanged(String text){
		thinlet.removeAll(comboBox);
		Collection<CommunityHealthWorker> chws = chwDao.getCommunityHealthWorkerByName(text, 30);
		for(CommunityHealthWorker chw: chws){
			Object choice = thinlet.createComboboxChoice(chw.getName(), chw);
			thinlet.add(comboBox,choice);
		}
		if(chws.size() != 0){
			thinlet.setSelectedIndex(comboBox, 0);
			response = (CommunityHealthWorker) thinlet.getAttachedObject(thinlet.getSelectedItem(comboBox));
		}
		
	}
	
	public void selectionChanged(int index){
		hasChanged =true;
		if(index >=0){
			response = (CommunityHealthWorker) thinlet.getAttachedObject(thinlet.getItem(comboBox, index));
		}else{
			response = null;
		}
		super.responseChanged();
	}

	@Override
	public boolean isValid() {
		return hasResponse();
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void setRawResponse(CommunityHealthWorker chw) {
		thinlet.setText(comboBox, chw.getName());
		response = chw;
		textChanged(chw.getName());
	}
	
	@Override
	/** DOES NOTHING**/
	public void setStringResponse(String chw) {
	}
	
	@Override
	public CommunityHealthWorker getRawResponse(){
		return response;
	}
	
	public boolean hasResponse(){
		return getStringResponse()!=null;
	}
	
	@Override
	public String getStringResponse(){
		if(response!=null){
			return response.getName();
		}else{
			return null;
		}
	}

	public void setFieldForPerson(Person p) {
		Patient pat;
		try{
			pat = (Patient) p;
		}catch(Throwable t){return;}
		pat.setChw(getRawResponse());
	}
	
}
