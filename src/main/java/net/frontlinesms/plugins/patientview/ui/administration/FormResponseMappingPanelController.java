package net.frontlinesms.plugins.patientview.ui.administration;

import thinlet.Thinlet;

public class FormResponseMappingPanelController implements
		AdministrationTabPanel {

	public String getListItemTitle() {
		return "Map Form Responses";
	}

	public Object getPanel() {
		return Thinlet.create("panel");
	}

}
