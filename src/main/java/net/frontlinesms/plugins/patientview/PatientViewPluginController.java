package net.frontlinesms.plugins.patientview;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.patientview.listener.PatientViewFormListener;
import net.frontlinesms.plugins.patientview.listener.PatientViewMessageListener;
import net.frontlinesms.plugins.patientview.responsemapping.IncomingFormMatcher;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.PatientViewThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

@PluginControllerProperties(name="PatientView", iconPath="/icons/big_medic.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/patientview/patientview-spring-hibernate.xml",
		hibernateConfigPath ="classpath:net/frontlinesms/plugins/patientview/patientview.hibernate.cfg.xml",i18nKey="PatientView")
public class PatientViewPluginController extends BasePluginController{

	/** the {@link FrontlineSMS} instance that this plugin is attached to */
	private FrontlineSMS frontlineController;
	
	/** The application context used for fetching daos and other spring beans**/
	private ApplicationContext applicationContext;
	
	private static IncomingFormMatcher incomingFormMatcher;
	private PatientViewMessageListener messageListener;
	private PatientViewFormListener formListener; 
	private PatientViewThinletTabController tabController;
	
	/** 
	 * @see net.frontlinesms.plugins.BasePluginController#initThinletTab(net.frontlinesms.ui.UiGeneratorController)
	 */
	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		//PatientFlagListener flagListener = new PatientFlagListener(applicationContext, uiController);
		tabController = new PatientViewThinletTabController(this,uiController);
		return tabController.getTab();
	}

	/**
	 * @see net.frontlinesms.plugins.PluginController#deinit()
	 */
	public void deinit() {	}

	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static IncomingFormMatcher getFormMatcher(){
		return incomingFormMatcher;
	}
	/** 
	 * @see net.frontlinesms.plugins.PluginController#init(net.frontlinesms.FrontlineSMS, org.springframework.context.ApplicationContext)
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		this.applicationContext = applicationContext;
		UserSessionManager.getUserSessionManager().init(applicationContext);
		incomingFormMatcher = new IncomingFormMatcher(applicationContext);
		messageListener = new PatientViewMessageListener(applicationContext);
		formListener = new PatientViewFormListener(applicationContext);
	}
	
	public void stopListening(){
		messageListener.setListening(false);
		formListener.setListening(false);
	}
	
	public void startListening(){
		messageListener.setListening(true);
		formListener.setListening(true);
	}

	public void setTabController(PatientViewThinletTabController tabController) {
		this.tabController = tabController;
	}

	public PatientViewThinletTabController getTabController() {
		return tabController;
	}
}
