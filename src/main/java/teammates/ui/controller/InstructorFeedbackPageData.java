package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;

public class InstructorFeedbackPageData extends PageData {

	public InstructorFeedbackPageData(AccountAttributes account) {
		super(account);
	}

	public String courseIdForNewSession;
	public FeedbackSessionAttributes newFeedbackSession;
	public List<CourseDetailsBundle> courses;
	public List<EvaluationDetailsBundle> existingEvals;
	public List<FeedbackSessionDetailsBundle> existingSessions;		
	
	
	public ArrayList<String> getTimeZoneOptionsAsHtml(){
		return getTimeZoneOptionsAsHtml(
				newFeedbackSession == null
					? Common.UNINITIALIZED_DOUBLE 
					: newFeedbackSession.timeZone);
	}
	
	
	public ArrayList<String> getGracePeriodOptionsAsHtml(){
		return getGracePeriodOptionsAsHtml(
				newFeedbackSession == null 
					? Common.UNINITIALIZED_INT
					: newFeedbackSession.gracePeriod);
	}
	
	
	public ArrayList<String> getTimeOptionsAsHtml(boolean isStartTime){
		if(newFeedbackSession == null ) {
			return getTimeOptionsAsHtml(null);
		} else {
			return getTimeOptionsAsHtml(isStartTime? newFeedbackSession.startTime : newFeedbackSession.endTime);
		}
	}
	
	public ArrayList<String> getCourseIdOptions() {
		ArrayList<String> result = new ArrayList<String>();

		for (CourseDetailsBundle courseBundle : courses) {

			// True if this is a submission of the filled 'new evaluation' form
			// for this course:
			boolean isFilledFormForEvaluationInThisCourse = (newFeedbackSession != null)
					&& courseBundle.course.id.equals(newFeedbackSession.courseId);

			// True if this is for displaying an empty form for creating an
			// evaluation for this course:
			boolean isEmptyFormForEvaluationInThisCourse = (courseIdForNewSession != null)
					&& courseBundle.course.id.equals(courseIdForNewSession);

			String selectedAttribute = isFilledFormForEvaluationInThisCourse
					|| isEmptyFormForEvaluationInThisCourse ? " selected=\"selected\""
					: "";

			result.add("<option value=\"" + courseBundle.course.id + "\""
					+ selectedAttribute + ">" + courseBundle.course.id + "</option>");
		}
		return result;
	}
		

}
