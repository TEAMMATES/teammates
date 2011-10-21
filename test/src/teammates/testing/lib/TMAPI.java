package teammates.testing.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.testing.Config;
import teammates.testing.object.Course;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;
import teammates.testing.object.Submission;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class use REST protocol to interact directly with Teammates software to
 * make changes.
 * 
 * URL to interact: http://<teammates_url>/api
 * 
 * @author huy, wangsha
 * 
 */
public class TMAPI {

	private static Gson gson = new Gson();
	private static final String SUBMISSION_DATA_TAG_ORIGINAL = "original: ";
	private static final String SUBMISSION_DATA_TAG_NORMALIZED = "normalized: ";
	private static final String SUBMISSION_DATA_TAG_CLAIMED = "claimed: ";
	private static final String SUBMISSION_DATA_TAG_PERCEIVED = "perceived: ";
	private static final String SUBMISSION_DATA_TAG_CLAIMEDCOORD = "claimedCoord: ";
	private static final String SUBMISSION_DATA_TAG_PERCEIVEDCOORD = "perceivedCoord: ";

	/**
	 * Clean up all tables. Except for Coordinator table.
	 */
	public static void cleanup() {
		 // Check appengine the test is running on, disable total cleanup for live server
        // wangsha
        // September 8
        cleanupByCoordinator();

		/*
        if(Config.TEAMMATES_APP.equalsIgnoreCase(Config.TEAMMATES_LIVE_SITE)) {
                System.out.println("Total cleanup disabled for live site, redirect to total clean up by coordinator.");
                cleanupByCoordinator();
        }else{
                System.out.println("Perform total cleanning up ");
                HashMap<String, Object> params = createParamMap("cleanup");
                String paramsString = buildParamsString(params);

                makePOSTRequest(paramsString);
        }*/
       
	}
	
	 /**
     * Clean up everything related to the coordinator
     * 
     * @author wangsha
     * @date Sep 8, 2011
     */
    public static void cleanupByCoordinator() {
    	System.out.println("Clean up by coordinator");
            HashMap<String, Object> params = createParamMap("cleanup_by_coordinator");
            params.put("coordinator_id", Config.TEAMMATES_COORD_ID);
            String paramsString = buildParamsString(params);
            makePOSTRequest(paramsString);
    }

	/**
	 * Clean up everything related to courseID.
	 * 
	 * @param courseId
	 * @return
	 */
	public static void cleanupCourse(String courseId) {
		System.out.println("Cleaning up course " + courseId);
		HashMap<String, Object> params = createParamMap("cleanup_course");
		params.put("course_id", courseId);
		String paramsString = buildParamsString(params);

		makePOSTRequest(paramsString);

	}

	/**
	 * Create new course
	 */
	public static void createCourse(Course course) {
		System.out.println("Creating course.");

		HashMap<String, Object> params = createParamMap("course_add");
		params.put("google_id", "teammates.coord");
		params.put("course", course.toJSON());
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void createEvaluation(Evaluation eval) {
		System.out.println("Creating evaluation.");
		HashMap<String, Object> params = createParamMap("evaluation_add");
		params.put("evaluation", eval.toJSON());
		String paramsString = buildParamsString(params);
		// System.out.println(eval.toJSON());
		makePOSTRequest(paramsString);

	}

	public static void openEvaluation(String courseID, String evalName) {
		System.out.println("Opening evaluation.");

		HashMap<String, Object> params = createParamMap("evaluation_open");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void closeEvaluation(String courseID, String evalName) {
		System.out.println("Closing evaluation.");

		HashMap<String, Object> params = createParamMap("evaluation_close");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void enrollStudents(String courseId, List<Student> students) {
		System.out.println("Enrolling students.");

		HashMap<String, Object> params = createParamMap("enroll_students");

		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		String json_students = gson.toJson(students, listType);
		params.put("course_id", courseId);
		params.put("students", json_students);

		// System.out.println(json_students);

		makePOSTRequest(buildParamsString(params));
	}

	public static void registerStudents(String courseId, List<Student> students) {
		System.out.println("Register students.");

		HashMap<String, Object> params = createParamMap("register_students");

		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		String json_students = gson.toJson(students, listType);
		params.put("course_id", courseId);
		params.put("students", json_students);

		// System.out.println(json_students);

		makePOSTRequest(buildParamsString(params));

	}

	public static void publishEvaluation(String courseID, String evalName) {
		HashMap<String, Object> params = createParamMap("evaluation_publish");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void unpublishEvaluation(String courseID, String evalName) {
		HashMap<String, Object> params = createParamMap("evaluation_unpublish");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void remindStudents(ArrayList<Student> students) {
		// TODO Auto-generated method stub
	}

	public static void submitEvaluation(ArrayList<Submission> submissions) {
	}

	/**
	 * HAVE NOT TESTED
	 */
	public static void studentsJoinCourse(List<Student> students,
			String courseId) {
		// Go into database and fill feedback from this student.
		System.out.println("Joining course for students.");

		HashMap<String, Object> params = createParamMap("students_join_course");
		params.put("course_id", courseId);
		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		params.put("students", gson.toJson(students, listType));

		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	/**
	 * Submit feedbacks for a particular student
	 * 
	 * @prerequisite Evaluation must be open.
	 */
	public static void studentSubmitFeedbacks(Student student, String courseId,
			String evaluationName) {
		// Go into database and fill feedback from this student.
		System.out.println("Submitting feedback for student " + student.email);

		HashMap<String, Object> params = createParamMap("student_submit_feedbacks");
		params.put("course_id", courseId);
		params.put("evaluation_name", evaluationName);
		params.put("student_email", student.email);

		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void studentsSubmitFeedbacks(List<Student> students,
			String courseId, String evaluationName) {
		for (Student s : students) {
			studentSubmitFeedbacks(s, courseId, evaluationName);
		}
	}
	
	/**
	 * New Submission Function for Testing BumpRatio:
	 * @author xialin
	 * 
	 **/
	public static void studentsSubmitDynamicFeedbacks(List<Student> students, String courseId, String evaluationName, String[] submissionPoints) throws IOException{
		//TODO: validate submission data set.
		System.out.println("submit dynamic feedbacks");
		int i = 0;
		for (Student s : students) {
			String points = getSubmissionPoints(submissionPoints[i]);
			studentSubmitDynamicFeedbacks(s, courseId, evaluationName, points);
			i++;
		}
		
	}
	
	public static void studentSubmitDynamicFeedbacks(Student student, String courseId, String evaluationName, String points){

		HashMap<String, Object> params = createParamMap("student_submit_dynamic_feedbacks");
		params.put("course_id", courseId);
		params.put("evaluation_name", evaluationName);
		params.put("student_email", student.email);
		params.put("team_name", student.teamName);
		params.put("submission_points", points);

		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}
	

	


	/**
	 * Evaluation Points Calculation API
	 * @param 
	 * @author xialin
	 * Data structure: 
	 * "original: 100, 100, 100; normalized: 100, 100, 100; claimed: 100; perceived: 100; claimedCoord: 100"
	 **/
	//index 0: original
	private static String getSubmissionPoints(String submission){

		String original = submission.split("; ")[0];// "original: 100, 100, 100"
		String points = original.substring(SUBMISSION_DATA_TAG_ORIGINAL.length());// "100, 100, 100"
		return points;
	}
	//index 1: normalized
	public static List<String> coordGetPointsToOthers(String[] submissionPoints, int personIndex){

		String submission = submissionPoints[personIndex];
		String normalized = submission.split("; ")[1];
		normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED.length());
		String[] pointArray = normalized.split(", ");

		//remove self evaluation point:
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < pointArray.length; i++){
//			if(i != personIndex){
				list.add(pointArray[i]);
//			}
		}
		
		return list;
	}
	public static List<String> coordGetPointsFromOthers(Scenario sc, int personIndex){
		
		List<String> list = new ArrayList<String>();
		String[] submissionPoints = sc.submissionPoints;
		String teamName = sc.students.get(personIndex).teamName;
		int start = 0;
		int end = 0;
		boolean started = false;
		for(int i = 0; i < sc.students.size(); i++){
			if(sc.students.get(i).teamName.equalsIgnoreCase(teamName)){
				if(!started){
					started = true;
					start = i;
					end = i;
				}
				else{
					end++;
				}
			}
		}
		
		for(int i = start; i <= end; i++){
			//remove self evaluation:
			String submission = submissionPoints[i];
			String normalized = submission.split("; ")[1];
			normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED.length());
			String[] pointArray = normalized.split(", ");
			list.add(pointArray[personIndex-start]);
			
		}
		
		return list;
	}
	//index 2: claimed
	public static String studentGetClaimedPoints(String[] submissionPoints, int personIndex){
		//student should see his/her original submisison point:
		String submission = submissionPoints[personIndex];
		String claimed = submission.split("; ")[2];
		claimed = claimed.substring(SUBMISSION_DATA_TAG_CLAIMED.length());
		return claimed;
	}
	//index 3: perceived
	public static String studentGetPerceivedPoints(String[] submissionPoints, int personIndex){
		//two normalization steps involved:
		String submission = submissionPoints[personIndex];
		String perceived = submission.split("; ")[3];
		perceived = perceived.substring(SUBMISSION_DATA_TAG_PERCEIVED.length());
		return perceived;
	}
	//index 4: claimedCoord
	public static String coordGetClaimedPoints(String[] submissionPoints, int personIndex){
		String submission = submissionPoints[personIndex];
		String claimedCoord = submission.split("; ")[4];
		claimedCoord = claimedCoord.substring(SUBMISSION_DATA_TAG_CLAIMEDCOORD.length());
		return claimedCoord;
	}
	//index 5: perceivedCoord
	public static String coordGetPerceivedPoints(String[] submissionPoints, int personIndex){
		String submission = submissionPoints[personIndex];
		String perceived = submission.split("; ")[5];
		perceived = perceived.substring(SUBMISSION_DATA_TAG_PERCEIVEDCOORD.length());
		return perceived;
	}
	public static String coordGetPointDifference(String[] submissionPoints, int personIndex){
		
		String claimed = coordGetClaimedPoints(submissionPoints, personIndex);
		String perceived = coordGetPerceivedPoints(submissionPoints, personIndex);
		
		if(claimed.equals("N/A") || perceived.equals("N/A")){
			return "N/A";
		}
		else{
			int claimedPoint = Integer.valueOf(claimed);
			int perceivedPoint = Integer.valueOf(perceived);
			int diff = perceivedPoint - claimedPoint ;
			return String.valueOf(diff);
		}
	}
	
	
	
	/*
	public static String studentGetClaimedPoints(String[] submissionPoints, int personIndex){
	
		//student should see his/her original submisison point:
		String submission = getSubmissionPoints(submissionPoints[personIndex]);
		String[] pointsArray = submission.split(", ");
		int point = Integer.valueOf(pointsArray[personIndex]);
		return pointToString(point);

	}
	public static String studentGetPerceivedPoints(String[] submissionPoints, int personIndex){
		//two normalization steps involved:
		int[][] result2 = new int[submissionPoints.length][2];
		result2 = calculateSecondNormalization(submissionPoints);
		
		int perceived = result2[personIndex][1];
		int point = (perceived/10) * 10;
		return pointToString(point);
	}
	
	public static String coordGetClaimedPoints(String[] submissionPoints, int personIndex){
		int[][] result = new int[submissionPoints.length][];
		result = calculateFirstNormalization(submissionPoints);
		
		int point = result[personIndex][personIndex];
		return pointToString(point);
		
	}
	public static String coordGetPerceivedPoints(String[] submissionPoints, int personIndex){

		//two normalization steps involved:
		int[][] result2 = new int[submissionPoints.length][2];
		result2 = calculateSecondNormalization(submissionPoints);
		
		int perceived = result2[personIndex][1];
		return pointToString(perceived);
	}
	public static String[] coordGetPointsToOthers(String[] submissionPoints, int personIndex){

		int[][] result = new int[submissionPoints.length][];
		result = calculateFirstNormalization(submissionPoints);
		
		//get first reviewer's submission:
		String[] list = new String[result[personIndex].length];
		for(int i = 0; i < result[personIndex].length; i++){
			int point = result[personIndex][i];
			list[i] = pointToString(point);
		}
		
		return list;
	}
	public static String[] coordGetPointsFromOthers(String[] submissionPoints, int personIndex){
		int[][] result = new int[submissionPoints.length][];
		result = calculateFirstNormalization(submissionPoints);
		
		String[] list = new String[submissionPoints.length];
		for(int i = 0; i < result.length; i++){
			//get points given to first person:
			int average = result[i][personIndex];
			list[i] = pointToString(average);
		}
		
		return list;
	}
	*/
	
	
	/*
	private static int[][] calculateFirstNormalization(String[] submissionPoints){
		int[][] result = new int[submissionPoints.length][];
		
		for(int i = 0; i < submissionPoints.length; i++){
			//get reviewer's submission list:
			String submission = getSubmissionPoints(submissionPoints[i]);
			String[] pointsArray = submission.split(", ");
			float bumpRatio = 1;
			int total = 0;
			int count = 0;
			int[] firstNormalized = new int[pointsArray.length];
			
			//check each submission point:
			for(int j = 0; j < pointsArray.length; j++){
				int point = Integer.valueOf(pointsArray[j]);
				if(point != -999 && point != -101){
					total += point;
					count ++;
				}
			}
			if(total != 0){
				bumpRatio = (float) (100 * count)/total;
			}
			//get first normalization:
			for(int j = 0; j < pointsArray.length; j++){
				
				int point = Integer.valueOf(pointsArray[j]);//original point
				if(point != -999 && point != -101){
					point = Math.round(point * bumpRatio);//normalized point
				}
				firstNormalized[j] = point;
			}
			
			result[i] = firstNormalized;

		}
		
		return result;
	}

	private static int[][] calculateSecondNormalization(String[] submissionPoints){
		//second normalization should be based on first normalization:
		int[][] result = new int[submissionPoints.length][];
		result = calculateFirstNormalization(submissionPoints);
		
		//second normalization - for each team
		int[][] result2 = new int[submissionPoints.length][2];
		for(int i = 0; i < submissionPoints.length; i++){
			int total = 0;
			int count = 0;
			
			int claimed = result[i][i];
			for(int j = 0; j < submissionPoints.length; j++){
				//points get from others
				if(j != i){
					if(result[j][i] != -999 && result[j][i] != -101){
						total += result[j][i];
						count ++;
					}
				}
			}
			int average = Math.round(total/count);
			
			result2[i][0] = claimed;
			result2[i][1] = average;
		}
		//calculate team bumpRatio:
		int total = 0;
		int count = 0;
		float bumpRatio = 1;
		for(int i = 0; i < submissionPoints.length; i++){
			int average = result2[i][1];
			
			if(average != -999 && average != - 101){
				total += average;
				count ++;
			}
			
		}
		if(total != 0){
			bumpRatio = (float) (100 * count)/total;
		}
		//based on result 2:
		for(int i = 0; i < submissionPoints.length; i++){
			int average = result2[i][1];
			if(average != -999 && average != - 101){
				result2[i][1] = Math.round(average * bumpRatio);
			}
		}
		
		return result2;
	}
	
	private static String pointToString(int point){
		if(point == -999 || point == -101){
			return "N/A";
		}
		else{
			return String.valueOf(point);
		}
	}
	
	*/
	
	//Oct 12 end--------------------------------
	
	/**
	 * Mail Stress Testing
	 * 
	 * @param account
	 * @param size
	 * @author wangsha
	 */
	public static void mailStressTesting(String account, int size) {
		System.out.println("Mail testing " + account + ", size:" + size);

		HashMap<String, Object> params = createParamMap("email_stress_testing");
		params.put("account", account);
		params.put("size", size);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);

	}
	
	/*
	 * public static void firstStudentDidNotSubmitFeedbacks(List<Student>
	 * students, String courseId, String evaluationName) { students.remove(0);
	 * for (Student s : students) { studentSubmitFeedbacks(s, courseId,
	 * evaluationName); } }
	 */

	// ---------------------------------
	// PRIVATE HELPER FUNCTIONS
	// ---------------------------------

	/**
	 * Take a map and convert it to url-friendly querystring
	 */
	private static String buildParamsString(HashMap<String, Object> map) {
		try {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Object> e : map.entrySet()) {

				sb.append(URLEncoder.encode(e.getKey(), "UTF-8") + "="
						+ URLEncoder.encode(e.getValue().toString(), "UTF-8")
						+ "&");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return "";
		}
	}

	private static HashMap<String, Object> createParamMap(String string) {
		HashMap<String, Object> ans = new HashMap<String, Object>();
		ans.put("action", string);
		
		// API Authentication
		ans.put("tm_auth", Config.API_AUTH_CODE);

		return ans;
	}

	private static String makePOSTRequest(String data) {
		try {
			// http://teammates/api
			URL url = new URL(Config.TEAMMATES_URL + "api");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			wr.close();
			rd.close();

			return sb.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
