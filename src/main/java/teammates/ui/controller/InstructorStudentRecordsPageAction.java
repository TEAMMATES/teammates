package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentRecordsPageAction extends Action {
	
	private InstructorStudentRecordsPageData data;
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL); 
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		data = new InstructorStudentRecordsPageData(account);
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
		
		try {
			data.courseId = courseId;
			data.student = logic.getStudentForEmail(courseId, studentEmail);
			data.comments = logic.getCommentsForGiverAndReceiver(courseId, instructor.email, studentEmail);
			List<EvaluationAttributes> evals = logic.getEvaluationsListForInstructor(account.googleId);
			List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsListForInstructor(account.googleId);
			
			//Remove evaluations and feedbacks not from the courseId parameters
			//Can be removed later when we want to have unified view
			for(int i = evals.size() - 1; i >= 0; i--){
				if(!evals.get(i).courseId.equals(courseId)){
					evals.remove(i);
				}
			}
			
			for(int i = feedbacks.size() - 1; i >= 0; i--){
				if(!feedbacks.get(i).courseId.equals(courseId)){
					feedbacks.remove(i);
				}
			}
			
			data.sessions = new ArrayList<SessionAttributes>();
			data.sessions.addAll(evals);
			data.sessions.addAll(feedbacks);
			Collections.sort(data.sessions, SessionAttributes.DESCENDING_ORDER);
			CommentAttributes.sortCommentsByCreationTimeDescending(data.comments);

			data.results = new ArrayList<SessionResultsBundle>();
			for(SessionAttributes session : data.sessions){
				if(session instanceof EvaluationAttributes){
					data.results.add(logic.getEvaluationResultForStudent(
							courseId, session.getSessionName(), studentEmail));
				} else if(session instanceof FeedbackSessionAttributes){
					data.results.add(logic.getFeedbackSessionResultsForInstructor(
									session.getSessionName(), courseId,instructor.email));
				}
			}
			
			if(data.sessions.size() == 0){
				statusToUser.add(Const.StatusMessages.INSTRUCTOR_NO_STUDENT_RECORDS);
			}
			
			statusToAdmin = "instructorStudentRecords Page Load<br>" + 
					"Viewing <span class=\"bold\">" + studentEmail + "'s</span> records " +
					"for Course <span class=\"bold\">[" + courseId + "]</span><br>" +
					"Number of sessions: " + data.sessions.size();
			
			return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, data);
			
		} catch (InvalidParametersException e) {
			setStatusForException(e); 
			return createShowPageResult(Const.ViewURIs.STATUS_MESSAGE, data);
		}
	}
}
