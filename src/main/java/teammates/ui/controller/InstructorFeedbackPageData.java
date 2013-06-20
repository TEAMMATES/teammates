package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;

public class InstructorFeedbackPageData extends PageData {

	public InstructorFeedbackPageData(AccountAttributes account) {
		super(account);
	}

	public String courseIdForNewSession;
	public FeedbackSessionAttributes newFeedbackSession;
	public List<CourseDetailsBundle> courses;
	public List<EvaluationDetailsBundle> existingEvals;
	public List<FeedbackSessionDetailsBundle> existingSessions;		
	
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
						(newFeedbackSession!=null && newFeedbackSession.timeZone==options[i]
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

		for (CourseDetailsBundle courseBundle : courses) {

			// True if this is a submission of the filled 'new evaluation' form
			// for this course:
			boolean isFilledFormForEvaluationInThisCourse = (newFeedbackSession != null)
					&& courseBundle.course.id.equals(newFeedbackSession.courseId);

			// True if this is for displaying an empty form for creating an
			// evaluation for this course:
			boolean isEmptyFormForEvaluationInThisCourse = (courseIdForNewSession != null)
					&& courseBundle.course.id.equals(courseIdForNewSession);

			String selectedAttribute = isFilledFormForEvaluationInThisCourse
					|| isEmptyFormForEvaluationInThisCourse ? " selected=\"selected\""
					: "";

			result.add("<option value=\"" + courseBundle.course.id + "\""
					+ selectedAttribute + ">" + courseBundle.course.id + "</option>");
		}
		return result;
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
		boolean isEditingExistingEvaluation = (newFeedbackSession!=null);
		if(isEditingExistingEvaluation){
			Date time = (isStart ? newFeedbackSession.startTime : newFeedbackSession.endTime);
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
		boolean isEditingExistingEvaluation = (newFeedbackSession!=null);
		if(isEditingExistingEvaluation){
			return gracePeriodOptionValue==newFeedbackSession.gracePeriod;
		} else {
			return gracePeriodOptionValue==defaultGracePeriod;
		}
	}

}
