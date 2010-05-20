package net.frontlinesms.plugins.patientview.ui.dialogs.searchareas;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class CollapsibleFormSearchArea extends EntitySearchArea<MedicForm>{
	
	public CollapsibleFormSearchArea(MedicForm entity, ExtendedThinlet uiController, SearchAreaDelegate<MedicForm> delegate,ApplicationContext appCon) {
		super(entity, uiController, delegate);
		formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		searchBarKeyPressed("");
	}

	private MedicFormDao formDao;
	
	@Override
	protected Collection<MedicForm> getEntitiesForString(String s) {
		return formDao.getMedicFormsByName(s);
	}

	@Override
	protected String getEntityName(MedicForm entity) {
		return entity.getName();
	}

	@Override
	protected String getEntityTypeName() {
		return InternationalisationUtils.getI18NString("medic.common.form");
	}
	
}
