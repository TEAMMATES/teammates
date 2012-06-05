package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.datatransfer.UserData;

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
	protected APIServlet server = new APIServlet();
	
	/**
	 * The user that is currently logged in, authenticated by Google
	 */
	protected UserData user;

	/**
	 * The userID that the admin wants to masquerade
	 */
	protected String requestedUser;
	
	/**
	 * The userID of the logged in user (<code>user.id</code>), or the userID
	 * requested by admin if in masquerade mode (<code>requestedUser</code>).
	 */
	protected String userID;
	
	/**
	 * The next URL to forward to after finished processing the request
	 */
	protected String nextUrl;

	/**
	 * The status message that want to be displayed
	 */
	protected String statusMessage;
	
	/**
	 * Flag whether there was an error, to be used to display status message style
	 * accordingly.
	 */
	protected boolean error;
	
	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		this.doPost(req,resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException{
		user = server.getLoggedInUser();
		
		requestedUser = req.getParameter(Common.PARAM_USER_ID);
		nextUrl = req.getParameter(Common.PARAM_NEXT_URL);
		
		statusMessage = null;
		error = false;
		
		if(isMasqueradeMode()){
			userID = requestedUser;
		} else {
			userID = user.id;
		}
		
		doPostAction(req, resp);
	}
	
	protected abstract void doPostAction(HttpServletRequest req,
										HttpServletResponse resp)
			throws IOException, ServletException;
	
	protected boolean isMasqueradeMode(){
		return user.isAdmin() && requestedUser!=null;
	}
}
