package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalPreviewAction extends Action {
	private StudentEvalSubmissionEditPageData data;
	
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
		Collections.sort(studentList, new StudentComparator());
		
		String studentEmail = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
		StudentAttributes student;
		if (studentEmail != null) {
			student = logic.getStudentForEmail(courseId, studentEmail);
		} else {
			if (!studentList.isEmpty()) {
				student = studentList.get(0);
			} else {
				statusToUser.add("The course " + courseId 
						+ " has yet to have any students so sessions cannot be previewed.");
				isError = true;
				return createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE);
			}
		}
		
		if (!student.googleId.isEmpty()) {
			AccountAttributes account = logic.getAccount(student.googleId);
			data = new StudentEvalSubmissionEditPageData(account);
		} else {
			data = new StudentEvalSubmissionEditPageData(student.name, studentEmail);
		}
		
		data.student = student;
		data.eval = logic.getEvaluation(courseId, evalName);
		Assumption.assertNotNull(data.eval);

		try{
			data.submissions = logic.getSubmissionsForEvaluationFromStudent(courseId, evalName, data.student.email);
			SubmissionAttributes.sortByReviewee(data.submissions);
			SubmissionAttributes.putSelfSubmissionFirst(data.submissions);
		} catch (InvalidParametersException e) {
			Assumption.fail("Invalid parameters not expected at this stage");
		}
		
		data.isPreview = true;
		data.studentList = studentList;
		
		statusToAdmin = "Preview evaluation as student (" + studentEmail + ")<br>" +
				"Session Name: " + evalName + "<br>" +
				"Course ID: " + courseId;
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_EVAL_SUBMISSION_EDIT, data);
		return response;
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
}
