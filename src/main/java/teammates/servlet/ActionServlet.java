package teammates.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.TeammatesException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.datatransfer.TeamData;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Abstract servlet to handle actions.
 * Child class must implement all the abstract methods,
 * which will be called from the doPost method in the superclass.
 * This is template pattern as said in:
 * http://stackoverflow.com/questions/7350297/good-method-to-make-it-obvious-that-an-overriden-method-should-call-super
 * @author Aldrian Obaja
 *
 */
public abstract class ActionServlet<T extends Helper> extends HttpServlet {
	
	protected static final Logger log = Common.getLogger();
	
	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		this.doPost(req,resp);
	}

	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException{
		
		// Check log in has been done in LoginFilter
		
		T helper = instantiateHelper();
		
		prepareHelper(req, helper);
		
		if(!doAuthenticateUser(req, resp, helper)) return;
		
		try{
			doAction(req, helper);
		} catch (EntityDoesNotExistException e){
			log.warning(e.getMessage());
			resp.sendRedirect(Common.JSP_ENTITY_NOT_FOUND_PAGE);
			return;
		} catch (Exception e){
			log.severe("Unexpected exception: "+TeammatesException.stackTraceToString(e));
			resp.sendRedirect(Common.JSP_ERROR_PAGE);
			return;
		}
		
		doCreateResponse(req, resp, helper);
	}
	
	/**
	 * Prepare the helper by filling these variables:
	 * <ul>
	 * <li>servlet</li>
	 * <li>user</li>
	 * <li>requestedUser</li>
	 * <li>userId - depends on the masquerade mode</li>
	 * <li>nextUrl - get from the request</li>
	 * <li>statusMessage - set to null</li>
	 * <li>error - set to false</li>
	 * </ul>
	 * @param req
	 * @param helper
	 */
	private void prepareHelper(HttpServletRequest req, T helper){		
		helper.server = new APIServlet();
		helper.user = helper.server.getLoggedInUser();
		
		helper.requestedUser = req.getParameter(Common.PARAM_USER_ID);
		helper.nextUrl = req.getParameter(Common.PARAM_NEXT_URL);
		
		helper.statusMessage = req.getParameter(Common.PARAM_STATUS_MESSAGE);
		helper.error = "true".equalsIgnoreCase(req.getParameter(Common.PARAM_ERROR));
		
		if(helper.isMasqueradeMode()){
			helper.userId = helper.requestedUser;
		} else {
			helper.userId = helper.user.id;
		}
	}
	
	/**
	 * Method to instantiate the helper.
	 * This method will be called at the beginning of request processing.
	 * @return
	 */
	protected abstract T instantiateHelper();
	
	/**
	 * Method to authenticate the user.
	 * Should return true if the user is authenticated, false otherwise.
	 * When this method returns false, the servlet does not call the doAction.
	 * This method is called after {@link #prepareHelper} method.
	 * @param req
	 * @param resp
	 * @param helper
	 * @return
	 * @throws IOException
	 */
	protected abstract boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, T helper) throws IOException;
	
	/**
	 * Method to do all the actions for this servlet.
	 * This method is supposed to only interact with API, and not to send response,
	 * which will be done in {@link #doCreateResponse}.
	 * This method is called directly after successful {@link #doAuthenticateUser}.
	 * It may include these steps:
	 * <ul>
	 * <li>Get parameters</li>
	 * <li>Validate parameters</li>
	 * <li>Send parameters and operation to server</li>
	 * <li>Put the response from API into the helper object</li>
	 * </ul>
	 * If exception is thrown, then the servlet will redirect the client to
	 * the error page with customized error message depending on the Exception
	 * @param req
	 * @param resp
	 * @param helper 
	 * @throws Exception
	 */
	protected abstract void doAction(HttpServletRequest req, T helper)
			throws EntityDoesNotExistException;
	
	/**
	 * Method to create the response to be sent back to the client,
	 * or to another servlet/JSP if the request is dispatched.
	 * If the request is to be dispatched to another servlet/JSP,
	 * this method must set the attribute "helper" in the request object.
	 * This method is called directly after {@link #doAction} method.
	 * @param req
	 * @param resp
	 * @param helper
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, T helper) throws ServletException, IOException;
	
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
	
	/**
	 * Sorts courses based on course ID
	 * @param courses
	 */
	protected void sortCourses(List<CourseData> courses){
		Collections.sort(courses,new Comparator<CourseData>(){
			public int compare(CourseData obj1, CourseData obj2){
				return obj1.id.compareTo(obj2.id);
			}
		});
	}
	
	/**
	 * Sorts evaluations based courseID (ascending), then by deadline (ascending),
	 * then by start time (ascending), then by evaluation name (ascending)
	 * The sort by CourseID part is to cater the case when this method is
	 * called with combined evaluations from many courses
	 * @param evals
	 */
	protected void sortEvaluationsByDeadline(List<EvaluationData> evals){
		Collections.sort(evals, new Comparator<EvaluationData>(){
			public int compare(EvaluationData obj1, EvaluationData obj2){
				int result = 0;
				if(result==0) result = obj1.endTime.after(obj2.endTime) ? 1
						: (obj1.endTime.before(obj2.endTime) ? -1 : 0);
				if(result==0) result = obj1.startTime.after(obj2.startTime) ? 1
						: (obj1.startTime.before(obj2.startTime) ? -1 : 0);
				if(result==0) result = obj1.course.compareTo(obj2.course);
				if(result==0) result = obj1.name.compareTo(obj2.name);
				return result;
			}
		});
	}
	
	/**
	 * Sorts teams based on team name
	 * @param teams
	 */
	protected void sortTeams(List<TeamData> teams){
		Collections.sort(teams,new Comparator<TeamData>(){
			public int compare(TeamData s1, TeamData s2){
				return (s1.name).compareTo(s2.name);
			}
		});
	}

	/**
	 * Sorts students based on student name then by email
	 * @param students
	 */
	protected void sortStudents(List<StudentData> students){
		Collections.sort(students,new Comparator<StudentData>(){
			public int compare(StudentData s1, StudentData s2){
				int result = s1.name.compareTo(s2.name);
				if(result==0) result = s1.email.compareTo(s2.email);
				return result;
			}
		});
	}
	
	/**
	 * Sorts submissions based on feedback (the first 70 chars)
	 * @param submissions
	 */
	protected void sortSubmissionsByFeedback(List<SubmissionData> submissions){
		Collections.sort(submissions, new Comparator<SubmissionData>(){
			public int compare(SubmissionData s1, SubmissionData s2){
				return s1.p2pFeedback.toString().compareTo(s2.p2pFeedback.toString());
			}
		});
	}
	
	/**
	 * Sorts submissions based on reviewer name then by email
	 * @param submissions
	 */
	protected void sortSubmissionsByReviewer(List<SubmissionData> submissions){
		Collections.sort(submissions, new Comparator<SubmissionData>(){
			public int compare(SubmissionData s1, SubmissionData s2){
				int result = s1.reviewerName.compareTo(s2.reviewerName);
				if(result==0) s1.reviewer.compareTo(s2.reviewer);
				return result;
			}
		});
	}
	
	/**
	 * Sorts submissions based on reviewee name then by email
	 * @param submissions
	 */
	protected void sortSubmissionsByReviewee(List<SubmissionData> submissions){
		Collections.sort(submissions, new Comparator<SubmissionData>(){
			public int compare(SubmissionData s1, SubmissionData s2){
				int result = s1.revieweeName.compareTo(s2.revieweeName);
				if(result==0) s1.reviewee.compareTo(s2.reviewee);
				return result;
			}
		});
	}
	
	/**
	 * Sorts submissions based on points (not the normalized one, although
	 * the relative ordering should be the same)
	 * @param submissions
	 */
	protected void sortSubmissionsByPoints(List<SubmissionData> submissions){
		Collections.sort(submissions, new Comparator<SubmissionData>(){
			public int compare(SubmissionData s1, SubmissionData s2){
				return Integer.valueOf(s1.points).compareTo(Integer.valueOf(s2.points));
			}
		});
	}
}
