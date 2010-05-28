package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import net.frontlinesms.ui.ExtendedThinlet;

public class TimeField extends TextBox<Date> {
	
	public TimeField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate ){
		super(thinlet, label,delegate);
	}
	
	@Override
	public boolean isValid() {
//			String input = getStringResponse().trim();
//			int hour = 0;
//			int minutes = 0;
//			int seconds =0;
//			Boolean isAM = null;
//			try{
//				hour = Integer.parseInt(input.substring(0,input.indexOf(":")));
//				minutes = Integer.parseInt(input.substring(input.indexOf(":")+1));
//			}catch(Throwable t){
//				try{
//					minutes = Integer.parseInt(input.substring(input.indexOf(":")+1,input.indexOf(" ")));
//					if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("AM")){
//						if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("PM")){
//							return false;
//						}else{
//							isAM=false;
//						}
//					}else{
//						isAM=true;
//					}
//				}catch(Throwable r){
//					try{
//						int firstColon = input.indexOf(":");
//						int secondColon =input.indexOf(":", firstColon+1);
//						minutes = Integer.parseInt(input.substring(firstColon+1,secondColon));
//						seconds = Integer.parseInt(input.substring(secondColon+1));
//					}catch(Throwable e){
//						try{
//							int firstColon = input.indexOf(":");
//							int secondColon =input.indexOf(":", firstColon+1);
//							seconds = Integer.parseInt(input.substring(secondColon+1, input.indexOf(" ")));
//							if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("AM")){
//								if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("PM")){
//									return false;
//								}else{
//									isAM=false;
//								}
//							}else{
//								isAM=true;
//							}
//						}catch(Throwable k){
//							return false;
//						}
//					}
//				}	
//			}
//			if(hour > 12 && isAM != null){
//				return false;
//			}
//			if(hour == 0 && isAM !=null){
//				return false;
//			}
//			if(hour > 24 || minutes > 60 ||seconds > 60){
//				return false;
//			}
//			return true;
		try {
			DateFormat.getTimeInstance().parse(getStringResponse());
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public Date getRawResponse() {
		try {
			return DateFormat.getTimeInstance().parse(getStringResponse());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setRawResponse(Date response) {
		setStringResponse(DateFormat.getTimeInstance(DateFormat.SHORT).format(response));
	}

}
