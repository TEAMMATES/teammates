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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.APIServlet;
import teammates.Common;
import teammates.testing.object.TeamFormingSession;
import teammates.testing.config.Config;
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
	private static final String SUBMISSION_DATA_TAG_DIFFERENCE = "diff: ";
	private static final String SUBMISSION_DATA_TAG_BUMPRATIO = "bumpratio: ";

	/**
	 * Clean up all tables. Except for Coordinator table.
	 */
	public static void cleanup() {
		 // Check appengine the test is running on, disable total cleanup for live server
        // wangsha
        // September 8
        cleanupByCoordinator();

		/*
        if(Config.inst().TEAMMATES_APP.equalsIgnoreCase(Config.inst().TEAMMATES_LIVE_SITE)) {
                System.out.println("Total cleanup disabled for live site, redirect to total clean up by coordinator.");
                cleanupByCoordinator();
        }else{
                System.out.println("Perform total cleaning up ");
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
    	cleanupByCoordinator(Config.inst().TEAMMATES_COORD_ID);
    }
    
    public static void cleanupByCoordinator(String coordId) {
    	HashMap<String, Object> params = createParamMap("cleanup_by_coordinator");
        params.put("coordinator_id", coordId);
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
		System.out.println("TMAPI.cleanupCourse() courseID = " + courseId);
		HashMap<String, Object> params = createParamMap("cleanup_course");
		params.put("course_id", courseId);
		String paramsString = buildParamsString(params);

		makePOSTRequest(paramsString);

	}

	/**
	 * Create new course
	 */
	public static void createCourse(Course course) {
		createCourse(course, "teammates.coord");
	}
	
	public static void createCourse(Course course, String coordId) {
		System.out.println("TMAPI Creating course: "+course.courseId);

		HashMap<String, Object> params = createParamMap("course_add");
		params.put("google_id", coordId);
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
	
	public static void disableEmail() {
		System.out.println("Disable sending email. ");
		HashMap<String, Object> params = createParamMap("disable_email");
		String paramsString = buildParamsString(params);

		makePOSTRequest(paramsString);

	}
	
	public static void enableEmail() {
		System.out.println("Enable sending email. ");
		HashMap<String, Object> params = createParamMap("enable_email");
		String paramsString = buildParamsString(params);

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
	public static void openTeamFormingSession(String courseID) {
        System.out.println("Opening team forming session.");

        HashMap<String, Object> params = createParamMap("teamformingsession_open");
        params.put("course_id", courseID);
        String paramsString = buildParamsString(params);
        makePOSTRequest(paramsString);
	}
	public static void createProfileOfExistingTeams(String courseId, String courseName, ArrayList teams) {
        System.out.println("Creating profiles of existing teams.");
        String teamProfile = "Please enter your team profile here.";
        
        for(int i = 0; i<teams.size(); i++){
                HashMap<String, Object> params = createParamMap("createteamprofiles");
                params.put("course_id", courseId);
                params.put("course_name", courseName);
                params.put("team_name", teams.get(i));
                params.put("team_profile", teamProfile);
                makePOSTRequest(buildParamsString(params));
        }
	}
	public static void createTeamFormingSession(TeamFormingSession teamForming) {
        System.out.println("Creating team forming session.");
        HashMap<String, Object> params = createParamMap("teamformingsession_add");
        params.put("teamformingsession", teamForming.toJSON());
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
	
	public static void activateAutomatedReminder(){
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_SYSTEM_ACTIVATE_AUTOMATED_REMINDER);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
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
	
	public static void updateBumpRatio() {
		// Go into database and fill feedback from this student.
		System.out.println("Update bump ratio");

		HashMap<String, Object> params = createParamMap("update_bump_ratio");
	
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
		System.out.println("submit dynamic feedbacks:\n\tstudents==>"+students.toString()+"\n\tcourseID==>"+courseId+"\n\tevaluationName==>"+evaluationName+"\n\tsubmissionPoints==>"+Arrays.toString(submissionPoints));
		int i = 0;
		for (Student s : students) {
			String points = getSubmissionPoints(submissionPoints[i]);
			String studentBumpRatio = calculateStudentBumpRatio(points);
			studentSubmitDynamicFeedbacks(s, courseId, evaluationName, points, studentBumpRatio);
			i++;
		}
		
	}
	
	public static void studentSubmitDynamicFeedbacks(Student student, String courseId, String evaluationName, String points, String studentBumpRatio){

		HashMap<String, Object> params = createParamMap("student_submit_dynamic_feedbacks");
		params.put("course_id", courseId);
		params.put("evaluation_name", evaluationName);
		params.put("student_email", student.email);
		params.put("team_name", student.teamName);
		params.put("submission_points", points);
		params.put("bumpRatio", studentBumpRatio);

		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}
	
	public static String calculateStudentBumpRatio(String points) {
		String[] pointArray = points.split(", ");
		int[] pointsPassed = new int[pointArray.length];
		
		int totalPoints = 0;
		
		for (int i = 0; i < pointArray.length; i++) {
			pointsPassed[i] = Integer.valueOf(pointArray[i]);
		}
		
		for (int i = 0; i < pointsPassed.length; i++) {
			// Exclude unsure and unfilled entries
			if(!((pointsPassed[i] == - 999) || (pointsPassed[i] == - 101))) {
				totalPoints = totalPoints + pointsPassed[i];
			}
		}
		float bumpRatio = (float) ((pointsPassed.length * 100.0) / (float) totalPoints);
		return String.valueOf(bumpRatio);
	}
	


	/**
	 * Evaluation Points Calculation API
	 * @param 
	 * @author xialin
	 * Data structure: 
	 * "original: 100, 100, 100; normalized: 100, 100, 100; claimed: 100; perceived: 100; claimedCoord: 100"
	 **/
	//index 0: original
	public static String getSubmissionPoints(String submission){

		String original = submission.split("; ")[0];// "original: 100, 100, 100"
		String points = original.substring(SUBMISSION_DATA_TAG_ORIGINAL.length());// "100, 100, 100"
		return points;
	}
	//index 1: normalized
	public static List<String> coordGetPointsToOthersTwoLines(String[] submissionPoints, int personIndex){

		String submission = submissionPoints[personIndex];
		String normalized = submission.split("; ")[1];
		normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED.length());
		String[] pointArray = normalized.split(", ");

		//remove self evaluation point:
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < pointArray.length; i++){
			String point = pointArray[i];
			if(point.length() > 12)//e.g. Equal Share + 20%
			point = point.substring(0, 11) + "\n" + point.substring(12);
			list.add(point);
		}
		
		return list;
	}
	
	//point list 2nd format
	public static List<String> coordGetPointsToOthersOneLine(String[] submissionPoints, int personIndex){

		String submission = submissionPoints[personIndex];
		String normalized = submission.split("; ")[1];
		normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED.length());
		String[] pointArray = normalized.split(", ");

		//remove self evaluation point:
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < pointArray.length; i++){
			String point = pointArray[i];
			if(point.length() > 12)//e.g. Equal Share + 20%
			point = point.substring(0, 11) + " " + point.substring(12);
			list.add(point);
		}
		
		return list;
	}
	
	public static List<String> coordGetPointsFromOthersTwoLines(Scenario sc, int personIndex){
		
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
			String point = pointArray[personIndex-start];
			if(point.length() > 12)//e.g. Equal Share + 20%
			point = point.substring(0, 11) + "\n" + point.substring(12);
			list.add(point);			
		}
		
		return list;
	}
	
	// 2nd format
	public static List<String> coordGetPointsFromOthersOneLine(Scenario sc, int personIndex){
		
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
			String point = pointArray[personIndex-start];
			if(point.length() > 12)//e.g. Equal Share + 20%
			point = point.substring(0, 11) + " " + point.substring(12);
			list.add(point);			
		}
		
		return list;
	}

	//index 2: claimed
	public static String studentGetClaimedPoints(String[] submissionPoints, int personIndex){
		//student should see his/her original submission point:
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
	//index 6: perceivedCoord - claimedCoord (diff)
	public static String coordGetPointDifference(String[] submissionPoints, int personIndex){
		String claimed = coordGetClaimedPoints(submissionPoints, personIndex);
		String perceived = coordGetPerceivedPoints(submissionPoints, personIndex);
		
		if(claimed.equals("N/A") || perceived.equals("N/A")){
			return "N/A";
		}
		else{		
			String submission = submissionPoints[personIndex];
			String difference = submission.split("; ")[6];
			difference = difference.substring(SUBMISSION_DATA_TAG_DIFFERENCE.length());
			return difference;
		}
	}
	
	//index 7: bump ratio
	public static String coordGetSubmissionBumpRatio(String[] submissionPoints, int personIndex){
		String submission =submissionPoints[personIndex];
		String bumpratio = submission.split("; ")[7];
		bumpratio = bumpratio.substring(SUBMISSION_DATA_TAG_BUMPRATIO.length());
		return bumpratio;
	}
	
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
		ans.put("tm_auth", Config.inst().API_AUTH_CODE);

		return ans;
	}
	
	//TODO: modify to use Json format?
	public static String[] getCoursesByCoordId(String coordId) {
		System.out.println("TMAPI Getting courses of coordinator:"+coordId);

		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_COURSES_BY_COORD);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		String paramsString = buildParamsString(params);
		String courseString = makePOSTRequest(paramsString);
		String[] coursesArray = courseString.split(" ");
		Arrays.sort(coursesArray);
		return coursesArray;
	}
	
	public static void deleteCourseByIdNonCascade(String courseId) {
		System.out.println("TMAPI deleting course (non cascade): " + courseId);
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		String paramsString = buildParamsString(params);
		String courseString = makePOSTRequest(paramsString);	
	}
	
	public static void deleteCourseByIdCascade(String courseId) {
		// TODO implement this fully
		 deleteCourseByIdNonCascade(courseId);
		
	}
	
	public static void createCoord(String coordId, String coordName,
			String coordEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_CREATE_COORD);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		params.put(APIServlet.PARAMETER_COORD_NAME, coordName);
		params.put(APIServlet.PARAMETER_COORD_EMAIL, coordEmail);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);	
	}

	/**
	 * Gets details of a coordinator.
	 * @param coordId
	 * @return Coordinator details in Json format. "null" if no such coord.
	 */
	public static String getCoordAsJason(String coordId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_COORD_AS_JSON);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		String paramsString = buildParamsString(params);
		String coordJsonString = makePOSTRequest(paramsString);
		return coordJsonString;
	}
	
	public static void deleteCoordByIdNonCascading(String coordId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_COORD_NON_CASCADE);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}
	
	public static String getCourseAsJason(String courseId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_COURSE_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		String paramsString = buildParamsString(params);
		String courseJsonString = makePOSTRequest(paramsString);
		return courseJsonString;
	}
	

	public static String persistNewDataBundle(String dataBundleJason) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_PERSIST_DATABUNDLE);
		params.put(APIServlet.PARAMETER_DATABUNDLE_JSON, dataBundleJason);
		String paramsString = buildParamsString(params);
		String status = makePOSTRequest(paramsString);
		return status;
	}
	

	public static String getStudentAsJason(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_STUDENT_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		params.put(APIServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String paramsString = buildParamsString(params);
		String studentJson = makePOSTRequest(paramsString);
		return studentJson;
	}
	
	public static String getEvaluationAsJason(String courseID, String evaluationName) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_EVALUATION_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		String paramsString = buildParamsString(params);
		String evaluationJson = makePOSTRequest(paramsString);
		return evaluationJson;
	}
	
	public static String getSubmissionAsJason(String courseID,
			String evaluationName, String reviewerEmail, String revieweeEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_SUBMISSION_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		params.put(APIServlet.PARAMETER_REVIEWER_EMAIL, reviewerEmail);
		params.put(APIServlet.PARAMETER_REVIEWEE_EMAIL, revieweeEmail);
		String paramsString = buildParamsString(params);
		String submissionJson = makePOSTRequest(paramsString);
		return submissionJson;
	}
	
	public static String getTfsAsJason(String courseID) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_TFS_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		String paramsString = buildParamsString(params);
		String evaluationJson = makePOSTRequest(paramsString);
		return evaluationJson;
	}
	
	public static String getTeamProfileAsJason(String courseID, String teamName) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_TEAM_PROFILE_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_TEAM_NAME, teamName);
		String paramsString = buildParamsString(params);
		String evaluationJson = makePOSTRequest(paramsString);
		return evaluationJson;
	}

	
	/**
	 * Sends data to server and returns the response 
	 * @param data
	 * @return
	 */
	private static String makePOSTRequest(String data) {
		try {
			// http://teammates/api
			URL url = new URL(Config.inst().TEAMMATES_URL + "api");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
