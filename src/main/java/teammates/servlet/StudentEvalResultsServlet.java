package teammates.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.SubmissionData;
import teammates.jsp.Helper;
import teammates.jsp.StudentEvalResultsHelper;

@SuppressWarnings("serial")
public class StudentEvalResultsServlet extends ActionServlet<StudentEvalResultsHelper> {

	@Override
	protected StudentEvalResultsHelper instantiateHelper() {
		return new StudentEvalResultsHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, StudentEvalResultsHelper helper)
			throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, StudentEvalResultsHelper helper)
			throws EntityDoesNotExistException {
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null || evalName==null){
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		helper.student = helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
		if(helper.student==null){
			helper.statusMessage = "You are not registered in the course "+Helper.escapeForHTML(courseID);
			helper.error = true;
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		
		helper.eval = helper.server.getEvaluation(courseID, evalName);
		
		try{
			helper.evalResult = helper.server.getEvaluationResultForStudent(courseID, evalName, helper.student.email);
			sortSubmissionsByPoints(helper.evalResult.incoming);
			helper.incoming = organizeSubmissions(helper.evalResult.incoming, helper);
			sortSubmissionsByPoints(helper.evalResult.outgoing);
			helper.outgoing = organizeSubmissions(helper.evalResult.outgoing, helper);
			sortSubmissionsByReviewee(helper.evalResult.selfEvaluations);
			helper.selfEvaluations = organizeSubmissions(helper.evalResult.selfEvaluations, helper);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
	}

	/**
	 * Put the self submission in front and return the
	 * list of submission excluding self submission
	 * @param subs
	 * @return
	 */
	private List<SubmissionData> organizeSubmissions(List<SubmissionData> subs, StudentEvalResultsHelper helper) {
		sortSubmissionsByPoints(subs);
		for(int i=0; i<subs.size(); i++){
			SubmissionData sub = subs.get(i);
			if(sub.reviewee.equals(sub.reviewer) && sub.reviewee.equals(helper.student.email)){
				subs.remove(sub);
				subs.add(0,sub);
				break;
			}
		}
		return subs.subList(1,subs.size());
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_RESULTS;
	}

}
