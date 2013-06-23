package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;

public class InstructorEvalEditPageData extends PageData {
	public EvaluationAttributes evaluation;

	public InstructorEvalEditPageData(AccountAttributes account) {
		super(account);
	}
	
	//TODO: methods below are repeated elsewhere
	
	/**
	 * Returns the timezone options as HTML code.
	 * None is selected, since the selection should only be done in client side.
	 */
	public ArrayList<String> getTimeZoneOptions(){
		double[] options = new double[]{-12,-11,-10,-9,-8,-7,-6,-5,-4.5,-4,-3.5,
										-3,-2,-1,0,1,2,3,3.5,4,4.5,5,5.5,5.75,6,
										7,8,9,10,11,12,13};
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<options.length; i++){
			String temp = "UTC";
			if(options[i]!=0){
				if((int)options[i]==options[i])
					temp+=String.format(" %+03d:00", (int)options[i]);
				else
					temp+=String.format(" %+03d:%02d", (int)options[i],
							(int)(Math.abs(options[i]-(int)options[i])*300/5));
			}
			result.add("<option value=\""+formatAsString(options[i])+"\"" +
						(evaluation!=null && evaluation.timeZone==options[i]
							? "selected=\"selected\""
							: "") +
						">"+temp+"</option>");
		}
		return result;
	}
	
	/**
	 * Returns the grace period options as HTML code
	 */
	public ArrayList<String> getGracePeriodOptions(){
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<=30; i+=5){
			result.add("<option value=\""+i+"\"" +
						(isGracePeriodToBeSelected(i) 
							? " selected=\"selected\"" : "") +
						">"+i+" mins</option>");
		}
		return result;
	}
	
	/**
	 * Returns the time options as HTML code
	 * By default the selected one is the last one.
	 * @param selectCurrentTime
	 */
	public ArrayList<String> getTimeOptions(boolean isStartTime){
		ArrayList<String> result = new ArrayList<String>();
		for(int i=1; i<=24; i++){
			result.add("<option value=\""+i+"\"" +
						(isTimeToBeSelected(i,isStartTime)
							? " selected=\"selected\""
							: "") +
						">" +
					   String.format("%04dH", i*100 - (i==24 ? 41 : 0)) +
					   "</option>");
		}
		return result;
	}
	
	
	private static String formatAsString(double num){
		if((int)num==num) {
			return ""+(int)num;
		} else {
			return ""+num;
		}
	}

	private boolean isTimeToBeSelected(int hour, boolean isStart){
		boolean isEditingExistingEvaluation = (evaluation!=null);
		if(isEditingExistingEvaluation){
			Date time = (isStart ? evaluation.startTime : evaluation.endTime);
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(time);
			if(cal.get(Calendar.MINUTE)==0){
				if(cal.get(Calendar.HOUR_OF_DAY)==hour) return true;
			} else {
				if(hour==24) return true;
			}
		} else {
			if(hour==24) return true;
		}
		return false;
	}

	private boolean isGracePeriodToBeSelected(int gracePeriodOptionValue){
		int defaultGracePeriod = 15;
		boolean isEditingExistingEvaluation = (evaluation!=null);
		if(isEditingExistingEvaluation){
			return gracePeriodOptionValue==evaluation.gracePeriod;
		} else {
			return gracePeriodOptionValue==defaultGracePeriod;
		}
	}

}
