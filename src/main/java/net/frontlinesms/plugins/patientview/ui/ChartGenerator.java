package net.frontlinesms.plugins.patientview.ui;


import java.util.Collection;
import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFieldResponse;

import org.jfree.data.time.Day;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;

public class ChartGenerator {

	public enum TimeSpan { MILLISECOND(),DAY(),MONTH(),YEAR();}
	public enum ChartType {YESVNO(),  YES(), NO(), NUMBER();}
	public enum CalcType {AVERAGE(),CUMULATIVE();}
	
	public ChartGenerator(){}
	
	public static TimeSeriesCollection generateDataSet(Collection<MedicFieldResponse> responses, CalcType calcType, TimeSpan timeSpan,ChartType chartType){
		Date previousDate = null;
		double currentTotal = 0;
		double currentTotal2 = 0;
		int numberOfItems = 0;
		int numberOfItems2 = 0;
		TimeSeries data=null;
		TimeSeries data2=null;
		//initialize the time series object
		if(chartType == ChartType.NUMBER){
			switch(timeSpan){
				case MILLISECOND: data = new TimeSeries("", Millisecond.class);break;
				case DAY: data = new TimeSeries("", Day.class); break;
				case MONTH: data = new TimeSeries("", Month.class); break;
				default: data = new TimeSeries("", Year.class); break;
			}
		}else{
			switch(timeSpan){
				case MILLISECOND: data = new TimeSeries("Yes", Millisecond.class);
					data2 = new TimeSeries("No", Millisecond.class);
				break;
				case DAY: data = new TimeSeries("Yes", Day.class);
					data2 = new TimeSeries("No", Day.class);
				break;
				case MONTH: data = new TimeSeries("Yes", Month.class); 
					data2 = new TimeSeries("No", Month.class);
				break;
				default: data = new TimeSeries("Yes", Year.class); 
					data2 = new TimeSeries("No", Year.class);
				break;
			}
			
		}
		//iterate through all the responses
		for(MedicFieldResponse mfr: responses){
			//you do the calculations different for numbers and boolean types
			if (chartType == ChartType.NUMBER) {
				if (isDifferent(mfr.getDateSubmitted(), previousDate, timeSpan)) {
					previousDate = mfr.getDateSubmitted();
					double result;
					if (calcType == CalcType.CUMULATIVE) {
						result = currentTotal;
					} else {
						// if you are doing an average, compute an average and
						// then reset the data points
						if(numberOfItems != 0){
							result = currentTotal / (double) numberOfItems;
						}else{
								result = currentTotal;
						}
						currentTotal = 0.0;
						numberOfItems = 0;
					}
					// add the data point
					switch (timeSpan) {
						case MILLISECOND: data.add(new Millisecond(mfr.getDateSubmitted()), result); break;
						case DAY: data.add(new Day(mfr.getDateSubmitted()), result); break;
						case MONTH: data.add(new Month(mfr.getDateSubmitted()), result); break;
						case YEAR: data.add(new Year(mfr.getDateSubmitted()), result); break;
					}
					System.out.println("Data Point added: " + result);
				}
				
				try {
					currentTotal += Double.parseDouble(mfr.getValue());
				} catch (Exception e) {
					currentTotal += Integer.parseInt(mfr.getValue());
				}
				numberOfItems++;
				System.out.println("Was not different");
			}else{
				if (isDifferent(mfr.getDateSubmitted(), previousDate, timeSpan)) {
					// first, switch the dates
					previousDate = mfr.getDateSubmitted();
					// if we are doing a cumulative chart it just takes the
					// current total and puts a data point
					// without resetting anything
					double result,result2;
					if (calcType == CalcType.CUMULATIVE) {
						result = currentTotal;
						result2 = currentTotal2;
					} else {
						// if you are doing an average, compute an average and
						// then reset the data points
						
						result = currentTotal;
						result2 = currentTotal2;
						currentTotal = 0.0;
						numberOfItems = 0;
						
						currentTotal2 = 0.0;
						numberOfItems2 = 0;
					}
					// add the data point 
					switch (timeSpan) {
						case MILLISECOND: data.add(new Millisecond(mfr.getDateSubmitted()), result);
							data2.add(new Millisecond(mfr.getDateSubmitted()), result2); 
						break;
						case DAY: data.add(new Day(mfr.getDateSubmitted()), result);
							data2.add(new Day(mfr.getDateSubmitted()), result2); 
						break;
						case MONTH: data.add(new Month(mfr.getDateSubmitted()), result); 
							data2.add(new Month(mfr.getDateSubmitted()), result2);
						break;
						case YEAR: data.add(new Year(mfr.getDateSubmitted()), result);
							data2.add(new Year(mfr.getDateSubmitted()), result2);
						break;
					}
				}
				if (mfr.getValue().equals("true")) {
					currentTotal++;
					numberOfItems++;
				} else {
					currentTotal2++;
					numberOfItems2++;
				}
			
			}
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		if(chartType == ChartType.NUMBER ||chartType == ChartType.YES ||chartType == ChartType.YESVNO)
			dataset.addSeries(data);
		
		if(chartType == ChartType.YESVNO ||chartType == ChartType.NO)
			dataset.addSeries(data2);
		return dataset;
	}
	
	public static boolean isDifferent(Date currentDate, Date previousDate, TimeSpan timeSpan){
		if(previousDate ==  null){
			return true;
		}
		boolean year = (currentDate.getYear() == previousDate.getYear());
		boolean month = (currentDate.getMonth() == previousDate.getMonth());
		boolean day = (currentDate.getDate() == previousDate.getDate());
		boolean milliseconds =  (currentDate.getTime() == previousDate.getTime());
		if(timeSpan == TimeSpan.YEAR){
			return !year;
		}else if(timeSpan == TimeSpan.MONTH){
			return !(year && month);
		}else if (timeSpan == TimeSpan.DAY){
			return !(year && month && day);
		}else if(timeSpan == TimeSpan.MILLISECOND){
			return !milliseconds;
		}
		return false;
	}
	
	
}
