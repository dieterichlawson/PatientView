package net.frontlinesms.plugins.medic.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.medic.data.domain.response.Response;
import net.frontlinesms.ui.UiGeneratorController;

public class ResultsTableController {
	private UiGeneratorController uiController;
	private Object table;
	private Object tableHeader;
	private MedicThinletTabController tabController;
	
	private Class currentEntity;
	private boolean isResponse;
	
	/** Objects for determining text width**/
	private ImageIcon icon;
	private Graphics graphics;
	private Font font;
	private FontMetrics metrics;
	
	public ResultsTableController(UiGeneratorController uiController, MedicThinletTabController tabController){
		boolean isResponse =false;
		this.uiController = uiController;
		if(tabController == null){
			System.out.println("Incoming is null");
		}
		this.tabController = tabController;
		table = this.uiController.find(tabController.getTab(),"resultTable");
		uiController.setAction(table, "tableSelectionChange(this)", null, this);
		uiController.setPerformMethod(table, "doubleClick(this)",null,this);
		//initialize stuff for determining font width
		icon = new ImageIcon();
		icon.setImage(new BufferedImage(10,10,BufferedImage.OPAQUE));
		graphics = icon.getImage().getGraphics();
		font= new Font("Sans Serif",Font.PLAIN,14);
		metrics = graphics.getFontMetrics(font);
		currentEntity = Math.class;
	}
	
	public void tableSelectionChange(Object table){
		Object entity = uiController.getAttachedObject(uiController.getSelectedItem(table));
		if(tabController == null){
			System.out.println("Tab controller null");
		}
		if(tabController.getDetailViewController() == null){
			System.out.println("Tab controller null");
		}
		tabController.getDetailViewController().selectionChanged(entity);
	}
	
	public void doubleClick(Object table){
		Object row = uiController.getSelectedItem(table);
		Object entity = uiController.getAttachedObject(row);
		tabController.drillDown(entity);
	}
	public void addRow(String text, Object entity){
		Object row = uiController.createTableRow(entity);
		Object cell = uiController.createTableCell(text);
		uiController.add(row,cell);
		uiController.add(table,row);
	}
	
	public void clearResults(){
		uiController.removeAll(table);
	}
	
	private void setCHWResults(List<CommunityHealthWorker> chws){
		int nameWidth = getStringWidth("Name");
		int ageWidth = getStringWidth("Age");
		for(CommunityHealthWorker chw: chws){
			String name = chw.getName();
			String age = chw.getAge() +"";
			String number = chw.getContactInfo().getMsisdn();
			nameWidth = Math.max(getStringWidth(name), nameWidth);
			ageWidth = Math.max(getStringWidth(age), ageWidth);
			Object row = uiController.createTableRow(chw);
			uiController.add(row,uiController.createTableCell(name));
			uiController.add(row,uiController.createTableCell(age));
			uiController.add(row,uiController.createTableCell(number));
			uiController.add(table,row);
		}	
		if(currentEntity == CommunityHealthWorker.class){
			uiController.add(table,tableHeader);
		}else{
			uiController.add(table,getCHWHeader(nameWidth,ageWidth));
		}
	}
	
	private void setPatientResults(List<Patient> patients){
		int nameWidth = getStringWidth("Name");
		int ageWidth = getStringWidth("Age");
		for(Patient p: patients){
			String name = p.getName();
			String age = p.getAge() +"";
			String chw = p.getChw().getName();
			nameWidth = Math.max(getStringWidth(name), nameWidth);
			ageWidth = Math.max(getStringWidth(age), ageWidth);
			Object row = uiController.createTableRow(p);
			uiController.add(row,uiController.createTableCell(name));
			uiController.add(row,uiController.createTableCell(age));
			uiController.add(row,uiController.createTableCell(chw));
			uiController.add(table,row);
		}
		if(currentEntity == Patient.class){
			uiController.add(table,tableHeader);
		}else{
			uiController.add(table,getPatientHeader(nameWidth,ageWidth));
		}
	}
	
	private void setFormResults(List<MedicForm> forms){
		for(MedicForm f: forms){
			Object row = uiController.createTableRow(f);
			uiController.add(row,uiController.createTableCell(f.getName()));
			uiController.add(table,row);
		}	
		if(currentEntity == MedicForm.class){
			uiController.add(table,tableHeader);
		}else{
			uiController.add(table,getFormHeader());
		}
	}
	
	
	private void setFieldResults(List<MedicField> fields){
		int nameWidth = getStringWidth("Name");
		for(MedicField f: fields){
			String name = f.getLabel();
			nameWidth = Math.max(getStringWidth(name),nameWidth);
			Object row = uiController.createTableRow(f);
			uiController.add(row,uiController.createTableCell(name));
			if(f instanceof MedicFormField){
				uiController.add(row,uiController.createTableCell(((MedicFormField) f).getForm().getName()));
			}	else{
				uiController.add(row,uiController.createTableCell("Detail View Field"));
			}
			uiController.add(table,row);
		}
		if(currentEntity == MedicFormField.class){
			uiController.add(table,tableHeader);
		}else{
		uiController.add(table,getFieldHeader(nameWidth));
		}
	}
	
	private void setMessageResults(List<MedicMessageResponse> messages){
		int submitterWidth = getStringWidth("Submitter");
		int dateSubmittedWidth = getStringWidth("Date Submitted");
		for(MedicMessageResponse m: messages){
			String submitter = m.getSubmitter().getName();
			String dateSubmitted = m.getDateSubmitted().toLocaleString();
			submitterWidth = Math.max(getStringWidth(submitter),submitterWidth);
			dateSubmittedWidth = Math.max(getStringWidth(dateSubmitted),submitterWidth);
			Object row = uiController.createTableRow(m);
			uiController.add(row,uiController.createTableCell(submitter));
			uiController.add(row,uiController.createTableCell(dateSubmitted));
			uiController.add(row,uiController.createTableCell(m.getMessageContent()));
			uiController.add(table,row);
		}
		if(currentEntity == MedicMessageResponse.class){
			uiController.add(table,tableHeader);
		}else{
		uiController.add(table,getMessageHeader(submitterWidth,dateSubmittedWidth));
		}
	}
	
	private void setFormResponseResults(List<MedicFormResponse> responses){
		int nameWidth = getStringWidth("Name");
		int submitterWidth = getStringWidth("Submitter");
		int subjectWidth = getStringWidth("Patient");
		for(MedicFormResponse r: responses){
			String name = r.getForm().getName();
			String submitter = r.getSubmitter().getName();
			String subject = r.getSubject().getName();
			nameWidth = Math.max(getStringWidth(name), nameWidth);
			submitterWidth = Math.max(getStringWidth(submitter), submitterWidth);
			subjectWidth = Math.max(getStringWidth(subject), subjectWidth);
			Object row = uiController.createTableRow(r);
			uiController.add(row,uiController.createTableCell(name));
			uiController.add(row,uiController.createTableCell(submitter));
			uiController.add(row,uiController.createTableCell(subject));
			uiController.add(row,uiController.createTableCell(r.getDateSubmitted().toLocaleString()));
			uiController.add(table,row);
		}	
		if(currentEntity == MedicFormResponse.class){
			uiController.add(table,tableHeader);
		}else{
			uiController.add(table,getFormResponseHeader(nameWidth,submitterWidth,subjectWidth));
		}
	}
	
	private void setFieldResponseResults(List<MedicFieldResponse> fields){
		int nameWidth = getStringWidth("Field Name");
		int responseWidth = getStringWidth("Response");
		int submitterWidth = getStringWidth("Submitter");
		int subjectWidth = getStringWidth("Patient");
		for(MedicFieldResponse f: fields){
			String name = f.getField().getLabel();
			String response = f.getValue().toString();
			String submitter = f.getSubmitter().getName();
			String subject = "";
			if(f.getSubject() == null){
				subject = "unknown";
			}else{
				subject = f.getSubject().getName();
			}
			nameWidth = Math.max(getStringWidth(name),nameWidth);
			responseWidth = Math.max(getStringWidth(response),responseWidth);
			submitterWidth = Math.max(getStringWidth(submitter), submitterWidth);
			subjectWidth = Math.max(getStringWidth(subject), subjectWidth);
			Object row = uiController.createTableRow(f);
			uiController.add(row,uiController.createTableCell(name));
			uiController.add(row,uiController.createTableCell(response));
			uiController.add(row,uiController.createTableCell(submitter));
			uiController.add(row,uiController.createTableCell(subject));
			uiController.add(row,uiController.createTableCell(f.getDateSubmitted().toLocaleString()));
			uiController.add(table,row);
		}
		if(currentEntity == MedicFieldResponse.class){
			uiController.add(table,tableHeader);
		}else{
			uiController.add(table,getFieldResponseHeader(nameWidth,responseWidth,submitterWidth,subjectWidth));
		}
	}
	
	//FIXME: this is janky. Clean it up
	public void setResults(List entities){
		if(entities != null && entities.size() != 0){
				uiController.removeAll(table);
			if(entities.get(0) instanceof Patient){
				setPatientResults(entities);
				currentEntity= Patient.class;
			}else if(entities.get(0) instanceof CommunityHealthWorker){
				setCHWResults(entities);
				currentEntity= CommunityHealthWorker.class;
			}else if(entities.get(0) instanceof MedicForm){
				setFormResults(entities);
				currentEntity= MedicForm.class;
			}else if(entities.get(0) instanceof MedicField){
				setFieldResults(entities);
				currentEntity= MedicFormField.class;
			}else if(entities.get(0) instanceof Response){
				setResponseResults(entities);
			}
		}else{
			uiController.removeAll(table);
			Object header = uiController.create("header");
			uiController.add(header,uiController.createColumn("No results to display", null));
			uiController.add(table,header);
			addRow("There were no results matching your query...", null);
		}
	}
	
	private void setResponseResults(List entities) {
		if(entities.get(0) instanceof MedicMessageResponse){
			setMessageResults((List<MedicMessageResponse>) entities);
			currentEntity = MedicMessageResponse.class;
		}else if(entities.get(0) instanceof MedicFormResponse){
			setFormResponseResults((List<MedicFormResponse>) entities);
			currentEntity = MedicForm.class;
		}else{
			setFieldResponseResults((List<MedicFieldResponse>) entities);
			currentEntity = MedicFieldResponse.class;
		}
	}

	//The following methods get the table headers for the different entity type
	private Object getCHWHeader(int nameWidth, int ageWidth){
		Object header = uiController.create("header");
		Object nameColumn = uiController.createColumn("Name", null);
		Object ageColumn = uiController.createColumn("Age", null);
		uiController.setInteger(nameColumn, "width", nameWidth);
		uiController.setInteger(ageColumn, "width", ageWidth);
		uiController.add(header,nameColumn);
		uiController.add(header,ageColumn);
		uiController.add(header,uiController.createColumn("Phone Number", null));
		uiController.setAction(header,"headerClicked()",null,this);
		tableHeader =header;
		return header;
	}
	
	private Object getPatientHeader(int nameWidth, int ageWidth){
		Object header = uiController.create("header");
		Object nameColumn = uiController.createColumn("Name", null);
		Object ageColumn = uiController.createColumn("Age", null);
		uiController.setInteger(nameColumn, "width", nameWidth);
		uiController.setInteger(ageColumn, "width", ageWidth);
		uiController.add(header,nameColumn);
		uiController.add(header,ageColumn);
		uiController.add(header,uiController.createColumn("CHW", null));
		uiController.setAction(header,"headerClicked()",null,this);
		tableHeader=header;
		return header;
	}
	
	private Object getFormHeader(){
		Object header = uiController.create("header");
		uiController.add(header,uiController.createColumn("Name", null));
		uiController.setAction(header,"headerClicked()",null,this);
		tableHeader=header;
		return header;
	}
	
	private Object getFieldHeader(int nameWidth){
		Object header = uiController.create("header");
		Object nameColumn = uiController.createColumn("Field Name", null);
		uiController.setInteger(nameColumn,"width",nameWidth);
		uiController.add(header,nameColumn);
		uiController.add(header,uiController.createColumn("Parent Form", null));
		uiController.setAction(header,"headerClicked()",null,this);
		tableHeader=header;
		return header;
	}
	
	
	private Object getFormResponseHeader(int nameWidth, int submitterWidth, int subjectWidth){
			Object header = uiController.create("header");
			Object nameColumn =uiController.createColumn("Form Name", null);
			Object submitterColumn = uiController.createColumn("Submitter", null);
		    Object subjectColumn = uiController.createColumn("Subject", null);
		    uiController.setInteger(nameColumn,"width",nameWidth);
		    uiController.setInteger(submitterColumn,"width",submitterWidth);
		    uiController.setInteger(subjectColumn,"width",subjectWidth);
			uiController.add(header,nameColumn);
			uiController.add(header,submitterColumn);
			uiController.add(header, subjectColumn);
			uiController.add(header,uiController.createColumn("Date Submitted", null));
			uiController.setAction(header,"headerClicked()",null,this);
			tableHeader=header;
			return header;	
	}
	
	private Object getFieldResponseHeader(int nameWidth, int responseWidth, int submitterWidth, int subjectWidth){
		Object header = uiController.create("header");
		Object nameColumn = uiController.createColumn("Field Name", null);
		Object responseColumn = uiController.createColumn("Response", null);
		Object submitterColumn = uiController.createColumn("Submitter", null);
		Object subjectColumn = uiController.createColumn("Subject",null);
		uiController.setInteger(nameColumn,"width",nameWidth);
		uiController.setInteger(responseColumn,"width",responseWidth);
		uiController.setInteger(submitterColumn,"width",submitterWidth);
		uiController.setInteger(subjectColumn,"width",subjectWidth);
		uiController.add(header,nameColumn);
		uiController.add(header,responseColumn);
		uiController.add(header,submitterColumn);
		uiController.add(header,subjectColumn);
		uiController.add(header,uiController.createColumn("Date Submitted", null));
		uiController.setAction(header,"headerClicked()",null,this);
		tableHeader=header;
		return header;	
	}
	
	private Object getMessageHeader(int submitterWidth, int dateSubmittedWidth){
		Object header = uiController.create("header");
		Object submitterColumn = uiController.createColumn("Submitter", null);
		Object dateSubmittedColumn = uiController.createColumn("Date Submitted", null);
		uiController.setInteger(submitterColumn,"width",submitterWidth);
		uiController.setInteger(dateSubmittedColumn,"width",dateSubmittedWidth);
		uiController.add(header,submitterColumn);
		uiController.add(header,dateSubmittedColumn);
		uiController.add(header,uiController.createColumn("Message", null));
		uiController.setAction(header,"headerClicked()",null,this);
		tableHeader=header;
		return header;
	}
	
	/** This is a weird workaround to get the width of string in size 12 plain Sans Serif **/
	public int getStringWidth(String text){
		return metrics.stringWidth(text);
	}
	
	public void headerClicked(){
		int index = uiController.getSelectedIndex(tableHeader);
		String sort = uiController.getChoice(uiController.getSelectedItem(tableHeader), "sort");
		boolean sortOrder = (sort.equals("ascent"))? true:false;
		tabController.getQueryGenerator().setSort(index, sortOrder);
	}
	
}
