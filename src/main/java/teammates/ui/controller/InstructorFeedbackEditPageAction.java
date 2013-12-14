package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

		InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
		
		data.session = logic.getFeedbackSession(feedbackSessionName, courseId);
				
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				data.session,
				true);		
		
		if (data.session == null) {
			throw new EntityDoesNotExistException("Feedback session: " +
					feedbackSessionName + "does not exist in course: "
					+ courseId + ".");
		}
		
		data.questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		
		for(FeedbackQuestionAttributes question : data.questions) {			
			data.questionHasResponses.put(question.getId(),
					logic.isQuestionHasResponses(question.getId()));
		}
		statusToAdmin = "instructorFeedbackEdit Page Load<br>"
				+ "Editing information for Feedback Session <span class=\"bold\">["
				+ feedbackSessionName + "]</span>" + "in Course: <span class=\"bold\">" + courseId + "]</span>";
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT, data);
	}

}
