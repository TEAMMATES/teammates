package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Abstract servlet to handle actions.
 * Child class must implement doPostAction, which will be called from the doPost
 * method in the superclass.
 * This is template pattern as said in:
 * http://stackoverflow.com/questions/7350297/good-method-to-make-it-obvious-that-an-overriden-method-should-call-super
 * @author Aldrian Obaja
 *
 */
public abstract class ActionServlet extends HttpServlet {
	
	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		this.doPost(req,resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException{
		// Check log in has been done in LoginFilter
		Helper helper = new Helper();
		helper.server = new APIServlet();
		helper.user = helper.server.getLoggedInUser();
		
		helper.requestedUser = req.getParameter(Common.PARAM_USER_ID);
		helper.nextUrl = req.getParameter(Common.PARAM_NEXT_URL);
		
		helper.statusMessage = null;
		helper.error = false;
		
		if(helper.isMasqueradeMode()){
			helper.userId = helper.requestedUser;
		} else {
			helper.userId = helper.user.id;
		}
		
		doPostAction(req, resp, helper);
	}
	
	/**
	 * The method to do specific actions
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	protected abstract void doPostAction(HttpServletRequest req,
										HttpServletResponse resp,
										Helper helper)
			throws IOException, ServletException;
	
	/**
	 * Returns the URL used to call this servlet.
	 * Reminder: This URL cannot be used to repeat sending POST data
	 * @param req
	 * @return
	 */
	protected String getRequestedURL(HttpServletRequest req){
		String link = req.getRequestURI();
		String query = req.getQueryString();
		if(query!=null) link+="?"+query;
		return link;
	}
}
