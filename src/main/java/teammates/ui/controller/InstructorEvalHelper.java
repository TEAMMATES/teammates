package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

public class InstructorEvalHelper extends Helper{
	public List<CourseDetailsBundle> courses;
	// This is the ID of the course the evaluation page should display, 
	//   when it loads for the user to fill in data for a new evaluation.
	public String courseIdForNewEvaluation;
	// This stores the evaluation details when a user submits a new evaluation
	//   to be stored.
	public EvaluationAttributes newEvaluationToBeCreated;
	public List<EvaluationDetailsBundle> evaluations;
	
	/**
	 * Returns the timezone options as HTML code.
	 * None is selected, since the selection should only be done in client side.
	 * @return
	 */
	public ArrayList<String> getTimeZoneOptions(){
		double[] options = new double[]{-12,-11,-10,-9,-8,-7,-6,-5,-4.5,-4,-3.5,
										-3,-2,-1,0,1,2,3,3.5,4,4.5,5,5.5,5.75,6,
										7,8,9,10,11,12,13};
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<options.length; i++){
			String temp = "UTC";
			if(options[i]!=0){
				if((int)options[i]==options[i])
					temp+=String.format(" %+03d:00", (int)options[i]);
				else
					temp+=String.format(" %+03d:%02d", (int)options[i],
							(int)(Math.abs(options[i]-(int)options[i])*300/5));
			}
			result.add("<option value=\""+formatAsString(options[i])+"\"" +
						(newEvaluationToBeCreated!=null && newEvaluationToBeCreated.timeZone==options[i]
							? "selected=\"selected\""
							: "") +
						">"+temp+"</option>");
		}
		return result;
	}
	
	/**
	 * Returns the grace period options as HTML code
	 * @return
	 */
	public ArrayList<String> getGracePeriodOptions(){
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<=30; i+=5){
			result.add("<option value=\""+i+"\"" +
						(isGracePeriodToBeSelected(i) 
							? " selected=\"selected\"" : "") +
						">"+i+" mins</option>");
		}
		return result;
	}
	
	/**
	 * Returns the time options as HTML code
	 * By default the selected one is the last one.
	 * @param selectCurrentTime
	 * @return
	 */
	public ArrayList<String> getTimeOptions(boolean isStartTime){
		ArrayList<String> result = new ArrayList<String>();
		for(int i=1; i<=24; i++){
			result.add("<option value=\""+i+"\"" +
						(isTimeToBeSelected(i,isStartTime)
							? " selected=\"selected\""
							: "") +
						">" +
					   String.format("%04dH", i*100 - (i==24 ? 41 : 0)) +
					   "</option>");
		}
		return result;
	}
	
	public ArrayList<String> getCourseIdOptions() {
		ArrayList<String> result = new ArrayList<String>();

		for (CourseDetailsBundle courseDetails : courses) {

			// True if this is a submission of the filled 'new evaluation' form
			// for this course:
			boolean isFilledFormForEvaluationInThisCourse = (newEvaluationToBeCreated != null)
					&& courseDetails.course.id.equals(newEvaluationToBeCreated.courseId);

			// True if this is for displaying an empty form for creating an
			// evaluation for this course:
			boolean isEmptyFormForEvaluationInThisCourse = (courseIdForNewEvaluation != null)
					&& courseDetails.course.id.equals(courseIdForNewEvaluation);

			String selectedAttribute = isFilledFormForEvaluationInThisCourse
					|| isEmptyFormForEvaluationInThisCourse ? " selected=\"selected\""
					: "";

			result.add("<option value=\"" + courseDetails.course.id + "\""
					+ selectedAttribute + ">" + courseDetails.course.id + "</option>");
		}
		return result;
	}
	
	public EvaluationAttributes createEvaluation(HttpServletRequest req) 
			throws EntityDoesNotExistException, EntityAlreadyExistsException, InvalidParametersException {
		
		try {
			newEvaluationToBeCreated = extractEvaluationData(req);
			server.createEvaluation(newEvaluationToBeCreated);
			statusMessage = Common.MESSAGE_EVALUATION_ADDED;

			EvaluationAttributes newEvaluationCreated = newEvaluationToBeCreated;
			newEvaluationToBeCreated = null;
			return newEvaluationCreated;
		
		} catch (EntityAlreadyExistsException e) {
			statusMessage = Common.MESSAGE_EVALUATION_EXISTS;
			error = true;
			throw e;
		
		} catch (InvalidParametersException e) {
			// This will cover conditions such as start/end date is invalid
			statusMessage = e.getMessage();
			error = true;
			throw e;
		}
		
	}
	
	public static EvaluationAttributes extractEvaluationData(HttpServletRequest req) {
		EvaluationAttributes newEval = new EvaluationAttributes();
		newEval.courseId = req.getParameter(Common.PARAM_COURSE_ID);
		newEval.name = req.getParameter(Common.PARAM_EVALUATION_NAME);
		newEval.p2pEnabled = Boolean.parseBoolean(req
				.getParameter(Common.PARAM_EVALUATION_COMMENTSENABLED));

		newEval.startTime = combineDateTime(
				req.getParameter(Common.PARAM_EVALUATION_START),
				req.getParameter(Common.PARAM_EVALUATION_STARTTIME));

		newEval.endTime = combineDateTime(
				req.getParameter(Common.PARAM_EVALUATION_DEADLINE),
				req.getParameter(Common.PARAM_EVALUATION_DEADLINETIME));

		String paramTimeZone = req
				.getParameter(Common.PARAM_EVALUATION_TIMEZONE);
		if (paramTimeZone != null) {
			newEval.timeZone = Double.parseDouble(paramTimeZone);
		}

		String paramGracePeriod = req
				.getParameter(Common.PARAM_EVALUATION_GRACEPERIOD);
		if (paramGracePeriod != null) {
			newEval.gracePeriod = Integer.parseInt(paramGracePeriod);
		}

		newEval.instructions = req
				.getParameter(Common.PARAM_EVALUATION_INSTRUCTIONS);

		return newEval;
	}
	
	public static Date combineDateTime(String inputDate, String inputTime) {
		if (inputDate == null || inputTime == null) {
			return null;
		}

		int inputTimeInt = 0;
		if (inputTime != null) {
			inputTimeInt = Integer.parseInt(inputTime) * 100;
		}
		return Common.convertToDate(inputDate, inputTimeInt);
	}

	public void loadEvaluationsList() 
				throws EntityDoesNotExistException {
			HashMap<String, CourseDetailsBundle> summary = 
					server.getCourseSummariesForInstructor(userId);
			courses = new ArrayList<CourseDetailsBundle>(summary.values());
			ActionServlet.sortDetailedCourses(courses);

			evaluations = server
					.getEvaluationsDetailsForInstructor(userId);
			ActionServlet.sortEvaluationsByDeadline(evaluations);
		
	}

	public void setStatusMessage() {
		String additionalMessage = null;
		if (courses.size() == 0 && !error) {
			additionalMessage = Common.MESSAGE_COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+userId);
		} else	if (evaluations.size() == 0 && !error
				&& !noEvaluationsVisibleDueToEventualConsistency()) {
			additionalMessage = Common.MESSAGE_EVALUATION_EMPTY;
		}
		
		if (additionalMessage != null) {
			if (statusMessage == null) {
				statusMessage = "";
			} else {
				statusMessage += "<br />";
			}
			statusMessage += additionalMessage;
		}
	}
	
	/**
	 * Helper to print the value of timezone the same as what javascript would
	 * produce.
	 * @param num
	 * @return
	 */
	private static String formatAsString(double num){
		if((int)num==num) {
			return ""+(int)num;
		} else {
			return ""+num;
		}
	}

	private boolean isTimeToBeSelected(int hour, boolean isStart){
		boolean isEditingExistingEvaluation = (newEvaluationToBeCreated!=null);
		if(isEditingExistingEvaluation){
			Date time = (isStart ? newEvaluationToBeCreated.startTime : newEvaluationToBeCreated.endTime);
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(time);
			if(cal.get(Calendar.MINUTE)==0){
				if(cal.get(Calendar.HOUR_OF_DAY)==hour) return true;
			} else {
				if(hour==24) return true;
			}
		} else {
			if(hour==24) return true;
		}
		return false;
	}

	private boolean isGracePeriodToBeSelected(int gracePeriodOptionValue){
		int defaultGracePeriod = 15;
		boolean isEditingExistingEvaluation = (newEvaluationToBeCreated!=null);
		if(isEditingExistingEvaluation){
			return gracePeriodOptionValue==newEvaluationToBeCreated.gracePeriod;
		} else {
			return gracePeriodOptionValue==defaultGracePeriod;
		}
	}

	private boolean noEvaluationsVisibleDueToEventualConsistency() {
		return statusMessage != null
				&& statusMessage.equals(Common.MESSAGE_EVALUATION_ADDED)
				&& evaluations.size()==0;
	}

}
