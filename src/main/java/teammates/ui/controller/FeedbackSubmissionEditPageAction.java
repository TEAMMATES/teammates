package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public abstract class FeedbackSubmissionEditPageAction extends Action {
	protected String courseId;
	protected String feedbackSessionName;
	protected FeedbackSubmissionEditPageData data;
	
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull(feedbackSessionName);
		
		if(!isSpecificUserJoinedCourse()){
			return createPleaseJoinCourseResponse(courseId);
		}
		
		verifyAccesibleForSpecificUser();
		
		String userEmailForCourse = getUserEmailForCourse();
		data = new FeedbackSubmissionEditPageData(account);
		data.bundle = getDataBundle(userEmailForCourse);		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName + " does not exist in "+courseId+".");
		}
		
		setStatusToAdmin();
		
		return createSpecificShowPageResult();
	}

	protected abstract boolean isSpecificUserJoinedCourse();
	
	protected abstract void verifyAccesibleForSpecificUser();
	
	protected abstract String getUserEmailForCourse();

	protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;

	protected abstract void setStatusToAdmin();
	
	protected abstract ShowPageResult createSpecificShowPageResult();
}
