package teammates.jsp;

import java.util.ArrayList;

import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;

public class CoordEvalHelper extends Helper{
	// Specific parameters
	public String coordID;
	public CourseData[] courses;
	public EvaluationData submittedEval;
	public ArrayList<EvaluationData> evaluations;
	
	public CoordEvalHelper(Helper helper){
		super(helper);
	}
	
	/**
	 * Returns the timezone options as HTML code.
	 * None is selected, since the selection should only be done in client side.
	 * @return
	 */
	public static ArrayList<String> getTimeZoneOptions(){
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
			result.add("<option value=\""+format(options[i])+"\">"+temp+"</option>");
		}
		return result;
	}
	
	/**
	 * Returns the grace period options as HTML code
	 * @return
	 */
	public static ArrayList<String> getGracePeriodOptions(){
		ArrayList<String> result = new ArrayList<String>();
		for(int i=5; i<=30; i+=5){
			result.add("<option value=\""+i+"\">"+i+" mins</option>");
		}
		return result;
	}
	
	/**
	 * Returns the time options as HTML code
	 * By default the selected one is the last one.
	 * @param selectCurrentTime
	 * @return
	 */
	public static ArrayList<String> getTimeOptions(){
		ArrayList<String> result = new ArrayList<String>();
		for(int i=1; i<=23; i++){
			result.add("<option value=\""+i+"\">" +
					   String.format("%04dH", i*100) +
					   "</option>");
		}
		result.add("<option value=\"24\" selected=\"selected\">2359H</option>");
		return result;
	}
	
	/**
	 * Helper to print the value of timezone the same as what javascript would
	 * produce.
	 * @param num
	 * @return
	 */
	private static String format(double num){
		if((int)num==num) return ""+(int)num;
		return ""+num;
	}
	
	public static void main(String[] args){
		for(String opt: getTimeZoneOptions()){
			System.out.println(opt);
		}
	}
}
