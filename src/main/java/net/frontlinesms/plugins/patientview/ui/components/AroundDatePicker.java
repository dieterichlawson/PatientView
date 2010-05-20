package net.frontlinesms.plugins.patientview.ui.components;

import java.util.Date;

import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.ui.UiGeneratorController;

public class AroundDatePicker implements DatePicker{

	private static int MILLISECONDS_PER_DAY =86400000;
	
	protected int fudgeDays;
	
	public Date getEndDate() {
		return new Date(dateField.getDateResponse().getTime() + (fudgeDays * MILLISECONDS_PER_DAY));
	}

	public Date getStartDate() {
		return new Date(dateField.getDateResponse().getTime() - (fudgeDays * MILLISECONDS_PER_DAY));
	}
	
	protected DateField dateField;
	
	public AroundDatePicker(UiGeneratorController uiController, int numberOfFudgeDays){
		dateField = new DateField(uiController, "Around");
		fudgeDays = numberOfFudgeDays;
	}
	
	public Object getMainPanel(){
		return dateField.getThinletPanel();
	}
}
