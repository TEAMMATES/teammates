package teammates.ui.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.Constants;
import teammates.logic.api.Logic;

@SuppressWarnings("serial")
/**
 * Servlet to handle Login
 */
public class LoginServlet extends HttpServlet {
	
	protected static final Logger log = Constants.getLogger();
	
	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		this.doPost(req,resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException{
		Logic server = new Logic();
		UserType user = server.getCurrentUser();
		if(req.getParameter(Constants.PARAM_LOGIN_INSTRUCTOR)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Constants.ACTION_INSTRUCTOR_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Constants.ACTION_INSTRUCTOR_HOME));
			}
		} else if(req.getParameter(Constants.PARAM_LOGIN_STUDENT)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Constants.ACTION_STUDENT_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Constants.ACTION_STUDENT_HOME));
			}
		} else if(req.getParameter(Constants.PARAM_LOGIN_ADMIN)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Constants.ACTION_ADMIN_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Constants.ACTION_ADMIN_HOME));
			}
		} else {
			resp.sendRedirect(Constants.VIEW_ERROR_PAGE);
		}
	}

	private boolean isMasqueradeMode(UserType user) {
		return user!=null;
	}
}
