package net.frontlinesms.plugins.patientview.importer.validation;

import net.frontlinesms.ui.i18n.InternationalisationUtils;

@SuppressWarnings("serial")
public class CsvValidationException extends Exception {

	public CsvValidationException(int lineNumber, String reason) {
		super();
		this.lineNumber = lineNumber;
		this.reason = reason;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	protected int lineNumber;
	protected String reason;
	
	public String toString(){
		return InternationalisationUtils.getI18NString("medic.importer.line")+" " + lineNumber+ ": " + reason;
	}
}
