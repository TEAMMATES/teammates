package teammates.ui.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.common.datatransfer.UserType;
import teammates.logic.api.Logic;

@SuppressWarnings("serial")
/**
 * Servlet to handle Login
 */
public class LoginServlet extends HttpServlet {
	
	protected static final Logger log = Common.getLogger();
	
	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		this.doPost(req,resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException{
		Logic server = new Logic();
		UserType user = server.getLoggedInUser();
		if(req.getParameter(Common.PARAM_LOGIN_COORD)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Common.PAGE_COORD_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Common.PAGE_COORD_HOME));
			}
		} else if(req.getParameter(Common.PARAM_LOGIN_STUDENT)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Common.PAGE_STUDENT_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Common.PAGE_STUDENT_HOME));
			}
		} else if(req.getParameter(Common.PARAM_LOGIN_ADMIN)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Common.PAGE_ADMIN_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Common.PAGE_ADMIN_HOME));
			}
		} else {
			resp.sendRedirect(Common.JSP_ERROR_PAGE);
		}
	}

	private boolean isMasqueradeMode(UserType user) {
		return user!=null;
	}
}
