package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.api.Logic;

/** An 'action' to be performed by the system. If the logged in user is allowed
 * to perform the requested action, this object can talk to the back end to
 * perform that action.
 */
public abstract class Action {
	protected static Logger log = Common.getLogger();
	
	protected Logic logic;
	
	/** This will be the admin user if the application is running under the masquerade mode. */
	public AccountAttributes loggedInUser;
	
	/** This is the 'nominal' user. Need not be the logged in user */
	public AccountAttributes account;
	
	/** The full request URL e.g., {@code /page/instructorHome?user=abc&course=c1} */
	String requestUrl;
	
	/** Parameters received with the request */
	Map<String, String[]> requestParameters;
	
	/** Execution status info to be shown to he admin (in 'activity log')*/
	protected String statusToAdmin; //TODO: make this a list?
	
	/** Execution status info to be shown to the user */
	protected List<String> statusToUser = new ArrayList<String>();
	
	/** Whether the execution completed without any errors */
	protected boolean isError = false;
	
	
	/** Initializes variables. 
	 * Aborts with an {@link UnauthorizedAccessException} if the user is not
	 * logged in or if a non-admin tried to masquerade as another user.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void init(HttpServletRequest req){
		
		requestUrl = Common.getRequestedURL(req);
		logic = new Logic();
		requestParameters = req.getParameterMap();
		
		//---- set error status forwarded from the previous action
		
		isError = getRequestParamAsBoolean(Common.PARAM_ERROR);
		
		//---- set logged in user ------------------------------------------

		UserType loggedInUserType = logic.getCurrentUser();
		
		if(loggedInUserType == null) {
			throw new UnauthorizedAccessException("User not logged in");
		}
		
		loggedInUser = logic.getAccount(loggedInUserType.id);
		
		if(loggedInUser==null){ //Unregistered user
			loggedInUser = new AccountAttributes();
			loggedInUser.googleId = loggedInUserType.id;
		}
		
		// ---------- set nominal user -------------------------------------
		
		String paramRequestedUserId = req.getParameter(Common.PARAM_USER_ID);
		
		if (!isMasqueradeModeRequested(loggedInUser.googleId, paramRequestedUserId)) {
			account = loggedInUser;
		
		} else if (loggedInUserType.isAdmin) {
			//Allowing admin to masquerade as another user
			account = logic.getAccount(paramRequestedUserId);
			if(account==null){ //Unregistered user
				account = new AccountAttributes();
				account.googleId = loggedInUserType.id;
			}
		
		} else {
			throw new UnauthorizedAccessException("User " + loggedInUserType.id
					+ " is trying to masquerade as " + paramRequestedUserId
					+ " without admin permission.");
		}
		
	}

	/**
	 * Executes the action (as implemented by a child class). Before passing 
	 * the result to the caller, it does some post processing: <br>
	 * 1. If the original request contained a URL to redirect after performing 
	 *    the action, the result will be replaced with a new 'redirect' type
	 *    result. Note: Redirection is not allowed to third-party destinations. <br>
	 * 2. User ID, error flag, and the status message will be added to the response,
	 *    to be encoded into the URL. The error flag is also added to the
	 *    {@code isError} flag in the {@link ActionResult} object.
	 */
	public ActionResult executeAndPostProcess() throws EntityDoesNotExistException, InvalidParametersException{
		
		//get the result from the child class.
		ActionResult response =  execute();
		
		//set error flag of the result
		response.isError = isError;
		
		//Override the result if a redirect was requested by the action requester
		String redirectUrl = getRequestParam(Common.PARAM_NEXT_URL);
		if(redirectUrl != null && new FieldValidator().isLegitimateRedirectUrl(redirectUrl)) {
			RedirectResult rr = new RedirectResult(redirectUrl, response.account, requestParameters, response.statusToUser);
			rr.isError = response.isError;
			response = rr;
		}
		
		//Set the common parameters for the response
		response.responseParams.put(Common.PARAM_USER_ID, account.googleId);
		response.responseParams.put(Common.PARAM_ERROR, ""+response.isError);
		if(!response.getStatusMessage().isEmpty()){
			response.responseParams.put(Common.PARAM_STATUS_MESSAGE, response.getStatusMessage());
		}
		
		return response;
	}

	protected abstract ActionResult execute() 
			throws EntityDoesNotExistException, InvalidParametersException;

	/**
	 * @return The log message in the special format used for generating 
	 *   the 'activity log' for the Admin.
	 */
	public String getLogMessage(){
		ActivityLogEntry activityLogEntry = new ActivityLogEntry(
				account, 
				isInMasqueradeMode(),
				statusToAdmin, 
				requestUrl);
		return activityLogEntry.generateLogMessage();
	}
	

	/**
	 * @return null if the specified parameter was not found in the request.
	 */
	public String getRequestParam(String paramName) { //TODO: rename to getRequestParamValue
		return Common.getValueFromParamMap(requestParameters, paramName);
	}
	
	/**
	 * @return null if the specified parameter was not found in the request.
	 */
	public String[] getRequestParamValues(String paramName) {
		return Common.getValuesFromParamMap(requestParameters, paramName);
	}
	
	public boolean getRequestParamAsBoolean(String paramName) {
		return Boolean.parseBoolean(Common.getValueFromParamMap(requestParameters, paramName));
	}

	/**
	 * Generates a {@link ShowPageResult} with the information in this object.
	 */
	public ShowPageResult createShowPageResult(String destination, PageData pageData) {
		return new ShowPageResult(
				destination, 
				account,
				requestParameters,
				pageData,
				statusToUser);
	}
	
	protected boolean notYetJoinedCourse(String courseId, String googleId) {
		return logic.getStudentForGoogleId(courseId, account.googleId) == null;
	}

	/**
	 * Generates a {@link RedirectResult} with the information in this object.
	 */
	public RedirectResult createRedirectResult(String destination) {
		return new RedirectResult(
				destination, 
				account,
				requestParameters,
				statusToUser);
	}

	protected ActionResult createPleaseJoinCourseResponse(String courseId) {
		String errorMessage = "You are not registered in the course "+PageData.escapeForHTML(courseId);
		statusToUser.add(errorMessage);
		isError = true;
		statusToAdmin = Common.LOG_SERVLET_ACTION_FAILURE + " : " + errorMessage; 
		return createRedirectResult(Common.PAGE_STUDENT_HOME);
	}

	private boolean isInMasqueradeMode() {
		return !loggedInUser.googleId.equals(account.googleId);
	}

	private boolean isMasqueradeModeRequested(String loggedInUserId, String requestedUserId) {
		return requestedUserId != null
				&& !requestedUserId.trim().equals("null")
				&& !loggedInUserId.equals(requestedUserId);
	}
	
	//===================== Utility methods used by some child classes========
	
	protected EvaluationAttributes extractEvaluationData() {
		//TODO: assert that values are not null
		EvaluationAttributes newEval = new EvaluationAttributes();
		newEval.courseId = getRequestParam(Common.PARAM_COURSE_ID);
		newEval.name = getRequestParam(Common.PARAM_EVALUATION_NAME);
		newEval.p2pEnabled = getRequestParamAsBoolean(Common.PARAM_EVALUATION_COMMENTSENABLED);

		newEval.startTime = Common.combineDateTime(
				getRequestParam(Common.PARAM_EVALUATION_START),
				getRequestParam(Common.PARAM_EVALUATION_STARTTIME));

		newEval.endTime = Common.combineDateTime(
				getRequestParam(Common.PARAM_EVALUATION_DEADLINE),
				getRequestParam(Common.PARAM_EVALUATION_DEADLINETIME));

		String paramTimeZone = getRequestParam(Common.PARAM_EVALUATION_TIMEZONE);
		if (paramTimeZone != null) {
			newEval.timeZone = Double.parseDouble(paramTimeZone);
		}

		String paramGracePeriod = getRequestParam(Common.PARAM_EVALUATION_GRACEPERIOD);
		if (paramGracePeriod != null) {
			newEval.gracePeriod = Integer.parseInt(paramGracePeriod);
		}

		newEval.instructions = getRequestParam(Common.PARAM_EVALUATION_INSTRUCTIONS);

		return newEval;
	}

}
