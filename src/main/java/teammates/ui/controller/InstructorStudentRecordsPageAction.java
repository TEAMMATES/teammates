package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentResultBundle;
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
			data.evaluations = logic.getEvaluationsListForInstructor(account.googleId);
			data.feedbacks = logic.getFeedbackSessionsListForInstructor(account.googleId);
			
			//Remove evaluations and feedbacks not from the courseId parameters
			//Can be removed later when we want to have unified view
			for(int i = data.evaluations.size() - 1; i >= 0; i--){
				if(!data.evaluations.get(i).courseId.equals(courseId)){
					data.evaluations.remove(i);
				}
			}
			
			for(int i = data.feedbacks.size() - 1; i >= 0; i--){
				if(!data.feedbacks.get(i).courseId.equals(courseId)){
					data.feedbacks.remove(i);
				}
			}
			
			data.studentEvaluationResults = new ArrayList<StudentResultBundle>();
			for(EvaluationAttributes evaluation: data.evaluations){
				data.studentEvaluationResults.add(
						logic.getEvaluationResultForStudent(courseId, evaluation.name, studentEmail));
			}
			
			data.studentFeedbackResults = new ArrayList<FeedbackSessionResultsBundle>();
			for(FeedbackSessionAttributes feedback: data.feedbacks){
				//TODO: This method call is too costly. It generates the result for the all students.
				data.studentFeedbackResults.add(
						logic.getFeedbackSessionResultsForInstructor(feedback.feedbackSessionName, courseId, instructor.email));
			}
			
			if(data.evaluations.size() == 0 && data.feedbacks.size() == 0){
				statusToUser.add(Const.StatusMessages.INSTRUCTOR_NO_STUDENT_RECORDS);
			}
			
			statusToAdmin = "instructorStudentRecords Page Load<br>" + 
					"Viewing <span class=\"bold\">" + studentEmail + "'s</span> records " +
					"for Course <span class=\"bold\">[" + courseId + "]</span><br>" +
					"Evaluations Size: " + data.evaluations.size() + 
					"Feedbacks Size: " + data.feedbacks.size();
			
			return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, data);
			
		} catch (InvalidParametersException e) {
			setStatusForException(e); 
			return createShowPageResult(Const.ViewURIs.STATUS_MESSAGE, data);
		}
	}
}
