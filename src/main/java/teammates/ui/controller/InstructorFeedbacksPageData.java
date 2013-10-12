package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;

public class InstructorFeedbacksPageData extends PageData {

	public InstructorFeedbacksPageData(AccountAttributes account) {
		super(account);
	}

	public String courseIdForNewSession;
	public FeedbackSessionAttributes newFeedbackSession;
	public List<CourseDetailsBundle> courses;
	public List<EvaluationAttributes> existingEvalSessions;
	public List<FeedbackSessionAttributes> existingFeedbackSessions;		
	
	
	public ArrayList<String> getTimeZoneOptionsAsHtml(){
		return getTimeZoneOptionsAsHtml(
				newFeedbackSession == null
					? Const.DOUBLE_UNINITIALIZED 
					: newFeedbackSession.timeZone);
	}
	
	
	public ArrayList<String> getGracePeriodOptionsAsHtml(){
		return getGracePeriodOptionsAsHtml(
				newFeedbackSession == null 
					? Const.INT_UNINITIALIZED
					: newFeedbackSession.gracePeriod);
	}
	
	public ArrayList<String> getCourseIdOptions() {
		ArrayList<String> result = new ArrayList<String>();

		for (CourseDetailsBundle courseBundle : courses) {

			// True if this is a submission of the filled 'new session' form
			// for this course:
			boolean isFilledFormForSessionInThisCourse = (newFeedbackSession != null)
					&& courseBundle.course.id.equals(newFeedbackSession.courseId);

			// True if this is for displaying an empty form for creating a 
			// session for this course:
			boolean isEmptyFormForSessionInThisCourse = (courseIdForNewSession != null)
					&& courseBundle.course.id.equals(courseIdForNewSession);

			String selectedAttribute = isFilledFormForSessionInThisCourse
					|| isEmptyFormForSessionInThisCourse ? " selected=\"selected\""
					: "";

			result.add("<option value=\"" + courseBundle.course.id + "\""
					+ selectedAttribute + ">" + courseBundle.course.id + "</option>");
		}
		return result;
	}
		

}
