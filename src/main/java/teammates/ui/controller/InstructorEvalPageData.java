package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;

public class InstructorEvalPageData extends PageData {
	public String courseIdForNewEvaluation;
	public EvaluationAttributes newEvaluationToBeCreated;
	public List<EvaluationAttributes> existingEvalSessions;
	public List<FeedbackSessionAttributes> existingFeedbackSessions;
	public List<CourseDetailsBundle> courses; //TODO: can we use a lighter data structure here?

	public InstructorEvalPageData(AccountAttributes account) {
		super(account);
	}
	
	
	public ArrayList<String> getTimeZoneOptionsAsHtml(){
		return getTimeZoneOptionsAsHtml(
				newEvaluationToBeCreated == null 
					? Const.DOUBLE_UNINITIALIZED 
					: newEvaluationToBeCreated.timeZone);
	}
	
	
	public ArrayList<String> getGracePeriodOptionsAsHtml(){
		return getGracePeriodOptionsAsHtml(
				newEvaluationToBeCreated == null 
					? Const.INT_UNINITIALIZED 
					: newEvaluationToBeCreated.gracePeriod);
	}
	
	
	public ArrayList<String> getTimeOptionsAsHtml(boolean isStartTime){
		if(newEvaluationToBeCreated == null ) {
			return getTimeOptionsAsHtml(null);
		} else {
			return getTimeOptionsAsHtml(
					isStartTime
						? newEvaluationToBeCreated.startTime 
						: newEvaluationToBeCreated.endTime);
		}
	}
	
	public ArrayList<String> getCourseIdOptions() {
		ArrayList<String> result = new ArrayList<String>();

		for (CourseDetailsBundle courseDetails : courses) {

			// True if this is a submission of the filled 'new evaluation' form
			// for this course:
			boolean isFilledFormForEvaluationInThisCourse = (newEvaluationToBeCreated != null)
					&& courseDetails.course.id.equals(newEvaluationToBeCreated.courseId);

			// True if this is for displaying an empty form for creating an
			// evaluation for this course:
			boolean isEmptyFormForEvaluationInThisCourse = (courseIdForNewEvaluation != null)
					&& courseDetails.course.id.equals(courseIdForNewEvaluation);

			String selectedAttribute = isFilledFormForEvaluationInThisCourse
					|| isEmptyFormForEvaluationInThisCourse ? " selected=\"selected\""
					: "";

			result.add("<option value=\"" + courseDetails.course.id + "\""
					+ selectedAttribute + ">" + courseDetails.course.id + "</option>");
		}
		return result;
	}
	

}
