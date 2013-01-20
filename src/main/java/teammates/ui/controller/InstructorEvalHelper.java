package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.EvaluationData;

public class InstructorEvalHelper extends Helper{
	public List<CourseData> courses;
	//This stores the evaluation details when a user submits an evaluation
	public EvaluationData submittedEval;
	//This stores the coure ID of the course the evaluation page should display, when it loads
	public EvaluationData initEval;
	public List<EvaluationData> evaluations;
	
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
						(submittedEval!=null && submittedEval.timeZone==options[i]
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
						(submittedEval!=null && submittedEval.gracePeriod==i
							? " selected=\"selected\""
							: "") +
						">" + i+" mins</option>");
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
						(checkTimeSelected(i,isStartTime)
							? " selected=\"selected\""
							: "") +
						">" +
					   String.format("%04dH", i*100 - (i==24 ? 41 : 0)) +
					   "</option>");
		}
		return result;
	}
	
	public ArrayList<String> getCourseIdOptions(){
		ArrayList<String> result = new ArrayList<String>();
		for(CourseData course: courses){
			//When an evaluation is submitted, submittedEval contains the selected value of the course ID
			boolean isPostRequest = submittedEval != null && course.id.equals(submittedEval.course);
			//When the evaluation page is opened, initEval contains the selected value of the course ID
			boolean isGetRequest = initEval != null && course.id.equals(initEval.course);
			
			result.add("<option value=\"" + course.id + "\"" +
					(isPostRequest || isGetRequest ? " selected=\"selected\"" : "" ) +
					">"+course.id+"</option>");
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
	
	private boolean checkTimeSelected(int hour, boolean isStart){
		if(submittedEval!=null){
			Date time = (isStart ? submittedEval.startTime : submittedEval.endTime);
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
}
