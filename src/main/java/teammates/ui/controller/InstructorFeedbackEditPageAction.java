package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
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
		
		data.studentList = logic.getStudentsForCourse(courseId);
		Collections.sort(data.studentList, new StudentComparator());
		
		data.instructorList = logic.getInstructorsForCourse(courseId);
		Collections.sort(data.instructorList, new InstructorComparator());
		
		statusToAdmin = "instructorFeedbackEdit Page Load<br>"
				+ "Editing information for Feedback Session <span class=\"bold\">["
				+ feedbackSessionName + "]</span>" + "in Course: <span class=\"bold\">" + courseId + "]</span>";
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT, data);
	}

	private class StudentComparator implements Comparator<StudentAttributes> {
		@Override
		public int compare(StudentAttributes s1, StudentAttributes s2) {
			if (s1.team.equals(s2.team)) {
				return s1.name.compareToIgnoreCase(s2.name);
			}
			return s1.team.compareToIgnoreCase(s2.team);
		}	
	}
	
	private class InstructorComparator implements Comparator<InstructorAttributes> {
		@Override
		public int compare(InstructorAttributes i1, InstructorAttributes i2) {
			return i1.name.compareToIgnoreCase(i2.name);
		}
	}
}
