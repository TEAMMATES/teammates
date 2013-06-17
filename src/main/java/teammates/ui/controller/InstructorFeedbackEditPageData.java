package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import teammates.common.FeedbackParticipantType;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;

public class InstructorFeedbackEditPageData extends PageData {

	public FeedbackSessionAttributes session;
	public FeedbackQuestionAttributes newQuestion;
	public List<FeedbackQuestionAttributes> questions;
	
	public InstructorFeedbackEditPageData(AccountAttributes account) {
		super(account);
	}
	
	public List<String> getParticipantOptions(FeedbackQuestionAttributes question, boolean isGiver){
		List<String> result = new ArrayList<String>();
		for(FeedbackParticipantType option : FeedbackParticipantType.values()) {
			if(isGiver && option.isValidGiver()) {
				result.add("<option value=\""+option.toString()+"\""
						+(question!=null && question.giverType==option
								? " selected=\"selected\"" : "")
						+">"+option.toDisplayGiverName()+"</option>");
			} else if(!isGiver && option.isValidRecipient()) {
				result.add("<option value=\""+option.toString()+"\""
						+(question!=null && question.recipientType==option
								? " selected=\"selected\"" : "")
						+">"+option.toDisplayRecipientName()+"</option>");
			}
		}		
		return result;
	}
	
	/**
	 * Returns the timezone options as HTML code.
	 * None is selected, since the selection should only be done in client side.
	 * @return
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
						(session!=null && session.timeZone==options[i]
							? "selected=\"selected\""
							: "") +
						">"+temp+"</option>");
		}
		return result;
	}
	
	/**
	 * Returns the grace period options as HTML code
	 * @return
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
	 * @return
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

	/**
	 * Helper to print the value of timezone the same as what javascript would
	 * produce.
	 * @param num
	 * @return
	 */
	private static String formatAsString(double num){
		if((int)num==num) {
			return ""+(int)num;
		} else {
			return ""+num;
		}
	}
	
	private boolean isTimeToBeSelected(int hour, boolean isStart){
		boolean isEditingExistingEvaluation = (session!=null);
		if(isEditingExistingEvaluation){
			Date time = (isStart ? session.startTime : session.endTime);
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
		boolean isEditingExistingEvaluation = (session!=null);
		if(isEditingExistingEvaluation){
			return gracePeriodOptionValue==session.gracePeriod;
		} else {
			return gracePeriodOptionValue==defaultGracePeriod;
		}
	}
}