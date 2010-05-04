package net.frontlinesms.plugins.patientview.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
public abstract class PersonDashboard<P extends Person> extends Dashboard {

	protected P person;
	
	protected List<TabController> tabs;
	
	private static final String GO_BACK_BUTTON = "patientrecord.buttons.go.back";
	private static final String TITLE_LABLE = "patientrecord.labels.patient.record";
	
	
	public PersonDashboard(UiGeneratorController uiController, ApplicationContext appCon, P p) {
		super(uiController, appCon);
		this.person = p;
		tabs = new ArrayList<TabController>();
		initView();
	}
	
	protected abstract void init();

	public void initView(){
		init();
		//add all the tabs
		for(TabController tab: tabs){
			uiController.add(tabbedPanel,tab.getTab());
		}
		Object button = uiController.createButton(InternationalisationUtils.getI18NString(GO_BACK_BUTTON));
		uiController.setAction(button, "goBack()", null, this);
		uiController.setHAlign(button, Thinlet.LEFT);
		uiController.setVAlign(button, Thinlet.BOTTOM);
		uiController.setWeight(button, 1, 1);
		uiController.add(leftPanel,button);
	}
	
	public void goBack(){
		collapseDashboard();
	}
}
