package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.CourseDataDetails;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.EvaluationDataDetails;

public class InstructorEvalHelper extends Helper{
	public List<CourseDataDetails> courses;
	// This is the ID of the course the evaluation page should display, 
	//   when it loads for the user to fill in data for a new evaluation.
	public String courseIdForNewEvaluation;
	// This stores the evaluation details when a user submits a new evaluation
	//   to be stored.
	public EvaluationData newEvaluationToBeCreated;
	public List<EvaluationDataDetails> evaluations;
	
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

		for (CourseDataDetails courseDetails : courses) {

			// True if this is a submission of the filled 'new evaluation' form
			// for this course:
			boolean isFilledFormForEvaluationInThisCourse = (newEvaluationToBeCreated != null)
					&& courseDetails.course.id.equals(newEvaluationToBeCreated.course);

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
}
