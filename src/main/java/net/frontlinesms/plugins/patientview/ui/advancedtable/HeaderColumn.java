package net.frontlinesms.plugins.patientview.ui.advancedtable;

import java.util.ArrayList;
import java.util.List;

public class HeaderColumn {

	private String method;
	private String icon;
	private String label;
	
	public HeaderColumn(String method, String icon, String label) {
		super();
		this.method = method;
		this.icon = icon;
		this.label = label;
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public static List<HeaderColumn> createColumnList(String[] labels, String[] icons, String[] methods){
		ArrayList<HeaderColumn> columns = new ArrayList<HeaderColumn>();
		if(labels.length != icons.length || icons.length != methods.length){
			return null;
		}
		for(int i = 0; i < labels.length;i++){
			columns.add(new HeaderColumn(methods[i], icons[i], labels[i]));
		}
		return columns;
	}
}
