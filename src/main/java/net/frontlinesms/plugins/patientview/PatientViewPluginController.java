package net.frontlinesms.plugins.patientview;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.patientview.analysis.FormMatcher;
import net.frontlinesms.plugins.patientview.ui.PatientViewThinletTabController;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

@PluginControllerProperties(name="Patient View", iconPath="/icons/big_medic.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/patientview/patientview-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/patientview/patientview.hibernate.cfg.xml")
public class PatientViewPluginController extends BasePluginController{

	/** the {@link FrontlineSMS} instance that this plugin is attached to */
	private FrontlineSMS frontlineController;
	
	/** The application context used for fetching daos and other spring beans**/
	private ApplicationContext applicationContext;
	
	private static FormMatcher formMatcher;
	
	/** 
	 * @see net.frontlinesms.plugins.BasePluginController#initThinletTab(net.frontlinesms.ui.UiGeneratorController)
	 */
	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		PatientViewThinletTabController controller= new PatientViewThinletTabController(this,uiController);
		return controller.getTab();
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

	public static FormMatcher getFormMatcher(){
		return formMatcher;
	}
	/** 
	 * @see net.frontlinesms.plugins.PluginController#init(net.frontlinesms.FrontlineSMS, org.springframework.context.ApplicationContext)
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		
		this.frontlineController = frontlineController;
		this.applicationContext = applicationContext;
		UserSessionManager.getUserSessionManager().init(applicationContext);
		DummyDataGenerator ddg = new DummyDataGenerator(applicationContext);
		formMatcher = new FormMatcher(applicationContext);
		PatientViewMessageListener listener = new PatientViewMessageListener(applicationContext);
	}
}
