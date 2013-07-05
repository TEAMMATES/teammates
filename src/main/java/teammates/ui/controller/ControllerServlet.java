package teammates.ui.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;

import com.google.apphosting.api.DeadlineExceededException;
/**
 * Receives requests from the Browser, executes the matching action and sends 
 * the result back to the Browser. The result can be page to view or a request
 * for the Browser to send another request for a different follow up Action.   
 */
@SuppressWarnings("serial")
public class ControllerServlet extends HttpServlet {

	protected static final Logger log = Utils.getLogger();

	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		this.doPost(req, resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		try{
			/* We are using the Template Method Design Pattern here.
			 * This method contains the high level logic of the the request processing.
			 * Concrete details of the processing steps are to be implemented by child
			 * classes, based on request-specific needs.
			 */
			Action c = ActionFactory.getAction(req);
			ActionResult actionResult = c.executeAndPostProcess();
			actionResult.send(req, resp);
			
			// This is the log message that is used to generate the 'activity log' for the admin.
			log.info(c.getLogMessage());
			
		} catch (EntityDoesNotExistException e) {
			log.warning(ActivityLogEntry.generateServletActionFailureLogMessage(req, e));
			resp.sendRedirect(Const.ViewURIs.ENTITY_NOT_FOUND_PAGE);

		} catch (UnauthorizedAccessException e) {
			log.warning(ActivityLogEntry.generateServletActionFailureLogMessage(req, e));
			resp.sendRedirect(Const.ViewURIs.UNAUTHORIZED);

		} catch (DeadlineExceededException e) {
			//TODO: This exception is not caught because GAE kills the request soon after throwing it.
			MimeMessage email = new Logic().emailErrorReport(
					req.getServletPath(), 
					HttpRequestHelper.printRequestParameters(req), 
					e);
			log.severe(ActivityLogEntry.generateSystemErrorReportLogMessage(req, email)); 
			resp.sendRedirect(Const.ViewURIs.DEADLINE_EXCEEDED_ERROR_PAGE);

		//TODO: handle invalid parameters exception
		}  catch (Throwable e) {
			MimeMessage email = new Logic().emailErrorReport(
					req.getServletPath(), 
					HttpRequestHelper.printRequestParameters(req), 
					e);

			log.severe(ActivityLogEntry.generateSystemErrorReportLogMessage(req, email)); 
		    resp.sendRedirect(Const.ViewURIs.ERROR_PAGE);
		} 
		
	}

	
}
