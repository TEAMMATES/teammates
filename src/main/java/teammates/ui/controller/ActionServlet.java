package teammates.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.api.Logic;

import com.google.apphosting.api.DeadlineExceededException;

@SuppressWarnings("serial")
/**
 * Abstract servlet to handle actions.
 * Child class must implement all the abstract methods,
 * which will be called from the doPost method in the superclass.
 * This is template pattern as said in:
 * http://stackoverflow.com/questions/7350297/good-method-to-make-it-obvious-that-an-overriden-method-should-call-super
 */
public abstract class ActionServlet<T extends Helper> extends HttpServlet {

	protected static final Logger log = Common.getLogger();
	protected boolean isPost = false;
	protected ActivityLogEntry activityLogEntry;

	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		this.doPost(req, resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		isPost = req.getMethod().equals("POST");
		if (isPost) {
			log.info("POST");
		} else {
			log.info("GET");
		}

		T helper = instantiateHelper();

		prepareHelper(req, helper);

		Level logLevel = null;
		String reqParam = Common.printRequestParameters(req);

		try {
			activityLogEntry = null;
			doAction(req, helper);
			logLevel = Level.INFO;
			doCreateResponse(req, resp, helper);
			
			log.log(logLevel, getMessageToBeLogged(req));

		} catch (EntityDoesNotExistException e) {
			logLevel = Level.WARNING;
			log.log(logLevel, generateServletActionFailureLogMessage(req, e) + Common.stackTraceToString(e));
			
			resp.sendRedirect(Common.JSP_ENTITY_NOT_FOUND_PAGE);

		} catch (UnauthorizedAccessException e) {
			logLevel = Level.WARNING;
			log.log(logLevel, generateServletActionFailureLogMessage(req, e)+ Common.stackTraceToString(e));
			
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);

		}  catch (DeadlineExceededException e) {
			MimeMessage email = helper.server.emailErrorReport(req.getServletPath(), reqParam, (Throwable) e);
			
			log.severe(generateSystemErrorReportLogMessage(req, email) + Common.stackTraceToString(e));	
			
			resp.sendRedirect(Common.JSP_DEADLINE_EXCEEDED_ERROR_PAGE);

		}  catch (Throwable e) {
			MimeMessage email = helper.server.emailErrorReport(req.getServletPath(), reqParam, e);

			log.severe(generateSystemErrorReportLogMessage(req, email) + Common.stackTraceToString(e));	
						
		    resp.sendRedirect(Common.JSP_ERROR_PAGE);

		}
	}
	
	
	protected String generateServletActionFailureLogMessage(HttpServletRequest req, Exception e){
		String[] actionTaken = req.getServletPath().split("/");
		String action = req.getServletPath();
		if(actionTaken.length > 0) {
			action = actionTaken[actionTaken.length-1]; //retrieve last segment in path
		}
		String url = getRequestedURL(req);
        
        String message = "<span class=\"color_red\">Servlet Action failure in " + action + "<br>";
        message += e.getClass() + ": " + e.getMessage() + "<br>";
        message += Common.printRequestParameters(req) + "</span>";
        
        ActivityLogEntry exceptionLog = new ActivityLogEntry(action, Common.LOG_SERVLET_ACTION_FAILURE, true, null, message, url);
        
        return exceptionLog.generateLogMessage();
	}
	

	protected String generateSystemErrorReportLogMessage(HttpServletRequest req, MimeMessage errorEmail) {
		String[] actionTaken = req.getServletPath().split("/");
		String action = req.getServletPath();
		if(actionTaken.length > 0) {
			action = actionTaken[actionTaken.length-1]; //retrieve last segment in path
		}
		String url = getRequestedURL(req);
        
        String message = "";
        if(errorEmail != null){
        	try {
      			message += "<span class=\"color_red\">" + errorEmail.getSubject() + "</span><br>";
      			message += "<a href=\"#\" onclick=\"showHideErrorMessage('error" + errorEmail.hashCode() +"');\">Show/Hide Details >></a>";
      			message += "<br>";
      			message += "<span id=\"error" + errorEmail.hashCode() + "\" style=\"display: none;\">";
      			message += errorEmail.getContent().toString();
      			message += "</span>";
      		} catch (Exception e) {
      			message = "System Error. Unable to retrieve Email Report";
      		}
      	}
		
		ActivityLogEntry emailReportLog = new ActivityLogEntry(action, Common.LOG_SYSTEM_ERROR_REPORT, true, null, message, url);
		
		return emailReportLog.generateLogMessage();
	}

	/**
	 * Prepare the helper by filling these variables:
	 * <ul>
	 * <li>servlet</li>
	 * <li>user</li>
	 * <li>requestedUser</li>
	 * <li>userId - depends on the masquerade mode</li>
	 * <li>redirectUrl - get from the request</li>
	 * <li>statusMessage - set to null</li>
	 * <li>error - set to false</li>
	 * </ul>
	 * 
	 * @param req
	 * @param helper
	 */
	private void prepareHelper(HttpServletRequest req, T helper) {
		helper.server = new Logic();
		helper.user = helper.server.getCurrentUser();

		helper.requestedUser = req.getParameter(Common.PARAM_USER_ID);
		helper.redirectUrl = req.getParameter(Common.PARAM_NEXT_URL);

		helper.statusMessage = req.getParameter(Common.PARAM_STATUS_MESSAGE);
		helper.error = "true".equalsIgnoreCase(req
				.getParameter(Common.PARAM_ERROR));

		if (helper.isMasqueradeMode()) {
			helper.userId = helper.requestedUser;
		} else {
			helper.userId = helper.user.id;
		}
		
		if (helper.userId != null ){
			helper.account = helper.server.getAccount(helper.userId);
		}
	}

	/**
	 * Method to instantiate the helper. This method will be called at the
	 * beginning of request processing.
	 * 
	 * @return
	 */
	protected abstract T instantiateHelper();


	/**
	 * Method to do all the actions for this servlet. This method is supposed to
	 * only interact with API, and not to send response, which will be done in
	 * {@link #doCreateResponse}. This method is called directly after
	 * successful {@link #doAuthenticateUser}. It may include these steps:
	 * <ul>
	 * <li>Get parameters</li>
	 * <li>Validate parameters</li>
	 * <li>Send parameters and operation to server</li>
	 * <li>Put the response from API into the helper object</li>
	 * </ul>
	 * If exception is thrown, then the servlet will redirect the client to the
	 * error page with customized error message depending on the Exception
	 * 
	 * @param req
	 * @param resp
	 * @param helper
	 * @throws InvalidParametersException 
	 * @throws Exception
	 */
	protected abstract void doAction(HttpServletRequest req, T helper)
			throws EntityDoesNotExistException, InvalidParametersException;

	/**
	 * Method to retrieve an application log message from the servlet that is
	 * currently being executed. This log message will be logged in server, and is
	 * used for AdminActivityLog. 
	 */
	protected String getMessageToBeLogged(HttpServletRequest req){
		//Create a default activity log if the servlet did not create one, else just ask the activity log for the message
		if(activityLogEntry == null){
			String[] actionTaken = req.getServletPath().split("/");
			String action = req.getServletPath();
			if(actionTaken.length > 0) {
				action = actionTaken[actionTaken.length-1]; //retrieve last segment in path
			}
			String url = getRequestedURL(req);
			activityLogEntry = new ActivityLogEntry(action, Common.printRequestParameters(req), url); 
		}
		return activityLogEntry.generateLogMessage();
	}
	
	
	/**
	 * Method to create an activityLog based on the servlet
	 * Each servlet is supposed to implement the corresponding generateActivityLogEntryMessage 
	 * The content of the ActivityLogEntry is dependent on the servlet
	 * @param servletName The name of the servlet. Constant taken from Common.java
	 * @param action The action the servlet is performing. Constant taken from Common.java
	 * @param toShow Whether or not this log should be shown in the Admin Activity Log
	 * @param helper The helper of the servlet 
	 * @paran data Additional data required for generating the Activity Log Entry, if needed
	 */
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShow, Helper helper, String url, ArrayList<Object> data){
		UserType user = helper.server.getCurrentUser();
		AccountAttributes account = helper.server.getAccount(user.id);
		String message = generateActivityLogEntryMessage(servletName, action, data);
			
		return new ActivityLogEntry(servletName, action, toShow, account, message, url);
	}

	/**
	 * Each servlet is supposed to implement their own generateActivityLogEntryMessage 
	 * @param servletName The name of the servlet. Constant taken from Common.java
	 * @param action The action the servlet is performing. Constant taken from Common.java
	 * @param helper The helper of the servlet 
	 * @paran data Additional data required for generating the Activity Log Entry, if needed
	 * @return
	 */
	//TODO: remove parameters from this method. They don't seem to be necessary.
	protected abstract String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data);

	
	/**
	 * Helper method to generate the error messages within the activity log
	 * Used within generateActivityLogEntryMessage for each servlet
	 */
	//TODO: move this to the Helper class?
	public static String generateActivityLogEntryErrorMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		if (action.equals(Common.LOG_SERVLET_ACTION_FAILURE)) {
            String e = data.get(0).toString();
            message = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
            message += e + "</span>";
        } else {
        	message = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
		return message;
	}
	
	/**
	 * Method to redirect or forward the request to appropriate display handler.<br />
	 * If helper.redirectUrl is not null, it will redirect there. Otherwise it
	 * will forward the request to helper.forwardUrl. If the link to be
	 * forwarded is null, it will get the default link from
	 * {@link #getDefaultForwardUrl}.<br />
	 * For redirection, any status message according to the helper.statusMessage
	 * will be displayed together with the error status (helper.error) as new
	 * parameters in the redirect URL<br />
	 * This method will also append the requested user ID in case of masquerade
	 * mode by admin.<br />
	 * For forwarding, the helper object will be attached to the request as an
	 * attribute with name "helper". The forward URL is used as is without any
	 * modification.<br />
	 * This method is called directly after {@link #doAction} method.
	 * 
	 * @param req
	 * @param resp
	 * @param helper
	 * @throws ServletException
	 * @throws IOException
	 */
	protected final void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, T helper) throws ServletException,
			IOException {
		if (helper.redirectUrl != null) {
			helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,
					Common.PARAM_STATUS_MESSAGE, helper.statusMessage);
			if (helper.error) {
				helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,
						Common.PARAM_ERROR, "" + helper.error);
			}
			helper.redirectUrl = helper.processMasquerade(helper.redirectUrl);
			resp.sendRedirect(helper.redirectUrl);
		} else {
			if (helper.forwardUrl == null) {
				helper.forwardUrl = getDefaultForwardUrl();
			}
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.forwardUrl).forward(req, resp);
		}
	}

	/**
	 * Returns the default link to forward to in case the redirectUrl is null<br />
	 * This is usually the display URL, which is a link to real JSP file used to
	 * display the result of the actions performed in this servlet.
	 * 
	 * @return
	 */
	protected String getDefaultForwardUrl() {
		// Not used
		return "";
	}

	/**
	 * Returns the URL used to call this servlet. Reminder: This URL cannot be
	 * used to repeat sending POST data
	 * 
	 * @param req
	 * @return
	 */
	//TODO: move this to Helper
	public static String getRequestedURL(HttpServletRequest req) {
		String link = req.getRequestURI();
		String query = req.getQueryString();
		if (query != null)
			link += "?" + query;
		return link;
	}

	/**
	 * Sorts courses based on course ID
	 * 
	 * @param courses
	 */ //TODO: move this method to Helper class
	public static void sortDetailedCourses(List<CourseDetailsBundle> courses) {
		Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
			public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
				return obj1.course.id.compareTo(obj2.course.id);
			}
		});
	}
	
	protected void sortCourses(List<CourseAttributes> courses) {
		Collections.sort(courses, new Comparator<CourseAttributes>() {
			public int compare(CourseAttributes obj1, CourseAttributes obj2) {
				return obj1.id.compareTo(obj2.id);
			}
		});
	}

	/**
	 * Sorts evaluations based courseID (ascending), then by deadline
	 * (ascending), then by start time (ascending), then by evaluation name
	 * (ascending) The sort by CourseID part is to cater the case when this
	 * method is called with combined evaluations from many courses
	 * 
	 * @param evals
	 */
	//TODO: move this to Helper
	public static void sortEvaluationsByDeadline(List<EvaluationDetailsBundle> evals) {
		Collections.sort(evals, new Comparator<EvaluationDetailsBundle>() {
			public int compare(EvaluationDetailsBundle edd1, EvaluationDetailsBundle edd2) {
				EvaluationAttributes eval1 = edd1.evaluation;
				EvaluationAttributes eval2 = edd2.evaluation;
				int result = 0;
				if (result == 0)
					result = eval1.endTime.after(eval2.endTime) ? 1
							: (eval1.endTime.before(eval2.endTime) ? -1 : 0);
				if (result == 0)
					result = eval1.startTime.after(eval2.startTime) ? 1
							: (eval1.startTime.before(eval2.startTime) ? -1 : 0);
				if (result == 0)
					result = eval1.courseId.compareTo(eval2.courseId);
				if (result == 0)
					result = eval1.name.compareTo(eval2.name);
				return result;
			}
		});
	}


	/**
	 * Sorts students based on student name then by email
	 * 
	 * @param students
	 */
	protected void sortStudents(List<StudentAttributes> students) {
		Collections.sort(students, new Comparator<StudentAttributes>() {
			public int compare(StudentAttributes s1, StudentAttributes s2) {
				int result = s1.name.compareTo(s2.name);
				if (result == 0)
					result = s1.email.compareTo(s2.email);
				return result;
			}
		});
	}

	/**
	 * Sorts submissions based on feedback (the first 70 chars)
	 * 
	 * @param submissions
	 */
	protected void sortSubmissionsByFeedback(List<SubmissionAttributes> submissions) {
		Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
				return s1.p2pFeedback.toString().compareTo(
						s2.p2pFeedback.toString());
			}
		});
	}
	
	/**
	 * Sorts submissions based on justification (the first 70 chars)
	 * 
	 * @param submissions
	 */
	protected void sortSubmissionsByJustification(List<SubmissionAttributes> submissions) {
		Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
				return s1.justification.toString().compareTo(
						s2.justification.toString());
			}
		});
	}

	/**
	 * Sorts submissions based on reviewer name then by email
	 * 
	 * @param submissions
	 */
	protected void sortSubmissionsByReviewer(List<SubmissionAttributes> submissions) {
		Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
				int result = s1.details.reviewerName.compareTo(s2.details.reviewerName);
				if (result == 0)
					s1.reviewer.compareTo(s2.reviewer);
				return result;
			}
		});
	}

	/**
	 * Sorts submissions based on reviewee name then by email
	 * 
	 * @param submissions
	 */
	protected void sortSubmissionsByReviewee(List<SubmissionAttributes> submissions) {
		Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
				int result = s1.details.revieweeName.compareTo(s2.details.revieweeName);
				if (result == 0)
					s1.reviewee.compareTo(s2.reviewee);
				return result;
			}
		});
	}

	/**
	 * Sorts submissions based on points (not the normalized one, although the
	 * relative ordering should be the same)
	 * 
	 * @param submissions
	 */
	protected void sortSubmissionsByPoints(List<SubmissionAttributes> submissions) {
		Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
				return Integer.valueOf(s1.points).compareTo(
						Integer.valueOf(s2.points));
			}
		});
	}
	
}
