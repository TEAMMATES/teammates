package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorEvalAddAction extends InstructorEvalPageAction {
	
	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getCourse(courseId));
		
		EvaluationAttributes eval = extractEvaluationData();
		
		InstructorEvalPageData data = new InstructorEvalPageData(account);
		data.newEvaluationToBeCreated = eval;
		
		try {
			
			logic.createEvaluation(eval);
			
			data.courseIdForNewEvaluation = null;
			data.newEvaluationToBeCreated = null;
			
			statusToUser.add(Common.MESSAGE_EVALUATION_ADDED);
			statusToAdmin = "New Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
					"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
					"<span class=\"bold\">Instructions:</span> " + eval.instructions;
			
		} catch (EntityAlreadyExistsException e) {
			statusToUser.add(Common.MESSAGE_EVALUATION_EXISTS);
			isError = true;
			statusToAdmin = e.getMessage();
			
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			isError = true;
			statusToAdmin = e.getMessage();
		} 
		
		data.courses = loadCoursesList(account.googleId);
		data.evaluations = loadEvaluationsList(account.googleId);
		if (data.evaluations.size() == 0) {
			statusToUser.add(Common.MESSAGE_EVALUATION_EMPTY);
		}
		
		return createShowPageResult(Common.JSP_INSTRUCTOR_EVAL, data);
	}
	

	
}
