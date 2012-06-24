package teammates.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.Logic;
import teammates.datatransfer.UserData;

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
		UserData user = server.getLoggedInUser();
		if(req.getParameter(Common.PARAM_LOGIN_COORDINATOR)!=null){
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
		} else if(req.getParameter(Common.PARAM_LOGIN_ADMINISTRATOR)!=null){
			if(isMasqueradeMode(user)){
				resp.sendRedirect(Common.PAGE_ADMINISTRATOR_HOME);
			} else {
				resp.sendRedirect(Logic.getLoginUrl(Common.PAGE_ADMINISTRATOR_HOME));
			}
		} else {
			resp.sendRedirect(Common.JSP_ERROR_PAGE);
		}
	}

	private boolean isMasqueradeMode(UserData user) {
		return user!=null;
	}
}
