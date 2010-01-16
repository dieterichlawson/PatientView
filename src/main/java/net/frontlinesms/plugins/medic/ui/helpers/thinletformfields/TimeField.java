package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class TimeField extends TextBox {
	
	public static final String NAME = "timeField";
	
	public TimeField(ExtendedThinlet thinlet, String label){
		super(thinlet, label,NAME);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected TimeField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet, label,name);
	}
	
	@Override
	public boolean isValid() {
			String input = getResponse().trim();
			int hour = 0;
			int minutes = 0;
			int seconds =0;
			Boolean isAM = null;
			try{
				hour = Integer.parseInt(input.substring(0,input.indexOf(":")));
				minutes = Integer.parseInt(input.substring(input.indexOf(":")+1));
			}catch(Throwable t){
				try{
					minutes = Integer.parseInt(input.substring(input.indexOf(":")+1,input.indexOf(" ")));
					if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("AM")){
						if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("PM")){
							return false;
						}else{
							isAM=false;
						}
					}else{
						isAM=true;
					}
				}catch(Throwable r){
					try{
						int firstColon = input.indexOf(":");
						int secondColon =input.indexOf(":", firstColon+1);
						minutes = Integer.parseInt(input.substring(firstColon+1,secondColon));
						seconds = Integer.parseInt(input.substring(secondColon+1));
					}catch(Throwable e){
						try{
							int firstColon = input.indexOf(":");
							int secondColon =input.indexOf(":", firstColon+1);
							seconds = Integer.parseInt(input.substring(secondColon+1, input.indexOf(" ")));
							if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("AM")){
								if(!input.substring(input.indexOf(" ")+1).toUpperCase().equals("PM")){
									return false;
								}else{
									isAM=false;
								}
							}else{
								isAM=true;
							}
						}catch(Throwable k){
							return false;
						}
					}
				}	
			}
			if(hour > 12 && isAM != null){
				return false;
			}
			if(hour == 0 && isAM !=null){
				return false;
			}
			if(hour > 24 || minutes > 60 ||seconds > 60){
				return false;
			}
			return true;
	}

}
