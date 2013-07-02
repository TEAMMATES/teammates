package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Config;

public class InstructorEvalEditPageData extends PageData {
	public EvaluationAttributes evaluation;

	public InstructorEvalEditPageData(AccountAttributes account) {
		super(account);
	}
	
	
	public ArrayList<String> getTimeZoneOptionsAsHtml(){
		return getTimeZoneOptionsAsHtml(evaluation == null ? Config.UNINITIALIZED_DOUBLE : evaluation.timeZone);
	}
	
	
	public ArrayList<String> getGracePeriodOptionsAsHtml(){
		return getGracePeriodOptionsAsHtml( evaluation==null ? Config.UNINITIALIZED_INT : evaluation.gracePeriod);
	}
	
	
	public ArrayList<String> getTimeOptionsAsHtml(boolean isStartTime){
		if(evaluation == null ) {
			return getTimeOptionsAsHtml(null);
		} else {
			return getTimeOptionsAsHtml(isStartTime? evaluation.startTime : evaluation.endTime);
		}
	}
	

}
