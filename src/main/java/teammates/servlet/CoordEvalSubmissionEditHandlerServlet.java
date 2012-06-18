package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
public class CoordEvalSubmissionEditHandlerServlet extends EvalSubmissionEditHandlerServlet {
	
	protected String getDisplayURL(){
		return Common.PAGE_COORD_EVAL;
	}
	
	protected String getEditSubmissionLink(){
		return Common.PAGE_COORD_EVAL_SUBMISSION_EDIT;
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

}
