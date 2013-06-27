package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.Common;
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
	
	public List<String> getParticipantOptions(
			FeedbackQuestionAttributes question, boolean isGiver){
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
	
	public ArrayList<String> getTimeZoneOptionsAsHtml(){
		return getTimeZoneOptionsAsHtml(session == null? Common.UNINITIALIZED_DOUBLE : session.timeZone);
	}
	
	
	public ArrayList<String> getGracePeriodOptionsAsHtml(){
		return getGracePeriodOptionsAsHtml(session == null ? Common.UNINITIALIZED_INT : session.gracePeriod);
	}
}