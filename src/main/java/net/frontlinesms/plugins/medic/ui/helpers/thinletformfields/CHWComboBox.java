package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.util.Collection;

import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateCommunityHealthWorkerDao;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

public class CHWComboBox extends ThinletFormField<CommunityHealthWorker>{
	
	private CommunityHealthWorker response;
	private Object comboBox;
	private HibernateCommunityHealthWorkerDao chwDao;
	private boolean hasChanged;
	public static final String NAME = "chwComboBox";
	
	public CHWComboBox(ExtendedThinlet thinlet, ApplicationContext appCon, CommunityHealthWorker chw) {
		super(thinlet, "CHW:", NAME);
		comboBox =thinlet.create("combobox");
		hasChanged=false;
		thinlet.setInsertMethod(comboBox,"textChanged(this.text)", null, this);
		thinlet.setRemoveMethod(comboBox, "textChanged(this.text)", null, this);
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		chwDao = (HibernateCommunityHealthWorkerDao) appCon.getBean("CHWDao");
		if(chw != null){
			thinlet.setText(comboBox, chw.getName());
			response = chw;
			textChanged(chw.getName());
		}
		thinlet.add(mainPanel,comboBox);
		thinlet.setInteger(comboBox, "weightx", 5);
		thinlet.setInteger(mainPanel, "colspan", 2);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected CHWComboBox(ExtendedThinlet thinlet, ApplicationContext appCon, CommunityHealthWorker chw, String name) {
		super(thinlet, "CHW:", name);
		comboBox =thinlet.create("combobox");
		hasChanged=false;
		thinlet.setInsertMethod(comboBox,"textChanged(this.text)", null, this);
		thinlet.setRemoveMethod(comboBox, "textChanged(this.text)", null, this);
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		chwDao = (HibernateCommunityHealthWorkerDao) appCon.getBean("CHWDao");
		if(chw != null){
			thinlet.setText(comboBox, chw.getName());
			response = chw;
			textChanged(chw.getName());
		}
		thinlet.add(mainPanel,comboBox);
		thinlet.setInteger(comboBox, "weightx", 5);
		thinlet.setInteger(mainPanel, "colspan", 2);
	}

	public void textChanged(String text){
		thinlet.removeAll(comboBox);
		Collection<CommunityHealthWorker> chws = chwDao.getCommunityHealthWorkerByName(text, 30);
		for(CommunityHealthWorker chw: chws){
			Object choice = thinlet.createComboboxChoice(chw.getName(), chw);
			thinlet.add(comboBox,choice);
		}
	}
	
	public void selectionChanged(int index){
		hasChanged =true;
		if(index >=0){
			response = (CommunityHealthWorker) thinlet.getAttachedObject(thinlet.getItem(comboBox, index));
		}else{
			response = null;
		}
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
	public void setResponse(String chw) {
	}
	
	@Override
	public CommunityHealthWorker getRawResponse(){
		return response;
	}
	
	@Override
	public String getResponse(){
		return response.getName();
	}
	
}
