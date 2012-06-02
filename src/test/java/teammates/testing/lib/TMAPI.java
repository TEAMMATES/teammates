package teammates.testing.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.APIServlet;
import teammates.Common;
import teammates.DataBundle;
import teammates.datatransfer.*;
import teammates.exception.EntityDoesNotExistException;
import teammates.exception.NotImplementedException;
import teammates.jdo.TeamProfile;
import teammates.testing.config.Config;
import teammates.testing.object.Course;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;
import teammates.testing.object.TeamFormingSession;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class provides an API for test driver to access TEAMMATES datastore
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

	private static final Logger log = Logger.getLogger(TMAPI.class.getName());


	

	// --------------------[System-level methods]-----------------------------

	public static String persistNewDataBundle(String dataBundleJason) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_PERSIST_DATABUNDLE);
		params.put(APIServlet.PARAMETER_DATABUNDLE_JSON, dataBundleJason);
		String status = makePOSTRequest(params);
		return status;
	}

	public static void deleteCoordinators(String jsonString) {
		Gson gson = Common.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, CoordData> coords = data.coords;
		for (CoordData coord : coords.values()) {
			deleteCoord(coord.id);
		}
	}

	// --------------------------[Coord-level methods]-------------------------

	public static String createCoord(CoordData coord) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.coords.put(coord.id, coord);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getCoordAsJason(String coordId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_COORD_AS_JSON);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		String coordJsonString = makePOSTRequest(params);
		return coordJsonString;
	}
	
	public static String editCoord(CoordData coord) throws NotImplementedException{
		throw new NotImplementedException("Not implemented because editing coordinators is not currently allowed");
	}

	public static String deleteCoord(String coordId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_COORD);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		String status = makePOSTRequest(params);
		return status;
	}

	public static void cleanupByCoordinator(String coordId)
			throws EntityDoesNotExistException {
		CoordData coord = Common.getTeammatesGson().fromJson(
				getCoordAsJason(coordId), CoordData.class);
		if (coord == null)
			throw new EntityDoesNotExistException(
					"Coordinator does not exist : " + coordId);
		deleteCoord(coordId);
		createCoord(coord);
	}

	// TODO: modify to use Json format?
	public static String[] getCoursesByCoordId(String coordId) {
		System.out.println("TMAPI Getting courses of coordinator:" + coordId);

		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_COURSES_BY_COORD);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		String courseString = makePOSTRequest(params);
		String[] coursesArray = {};
		if (Common.isWhiteSpace(courseString)) {
			return coursesArray;
		}
		coursesArray = courseString.trim().split(" ");
		Arrays.sort(coursesArray);
		return coursesArray;
	}

	// -------------------------[Course-level methods]-------------------------

	public static String createCourse(CourseData course) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.courses.put("dummy-key", course);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	//TODO: rename to Json
	public static String getCourseAsJason(String courseId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_COURSE_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		String courseJsonString = makePOSTRequest(params);
		return courseJsonString;
	}
	
	public static String editCourse(Course course) throws NotImplementedException {
		throw new NotImplementedException("Not implemented because editing courses is not currently allowed");
	}

	public static String deleteCourse(String courseId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_COURSE);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		String status = makePOSTRequest(params);
		return status;
	}

	// ------------------------[Student-level methods]-------------------------

	public static String createStudent(StudentData student) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.students.put("dummy-key", student);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getStudentAsJason(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_STUDENT_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		params.put(APIServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String studentJson = makePOSTRequest(params);
		return studentJson;
	}

	public static String editStudent(String originalEmail,
			StudentData student) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_EDIT_STUDENT);
		params.put(APIServlet.PARAMETER_STUDENT_EMAIL, originalEmail);
		params.put(APIServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(student));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteStudent(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_STUDENT);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		params.put(APIServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String status = makePOSTRequest(params);
		return status;
	}

	// ------------------------[Evaluation-level methods]-----------------

	public static String createEvaluation(EvaluationData evaluation) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.evaluations.put("dummy-key", evaluation);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getEvaluationAsJason(String courseID,
			String evaluationName) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_EVALUATION_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editEvaluation(EvaluationData evaluation) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_EDIT_EVALUATION);
		params.put(APIServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(evaluation));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteEvaluation(String courseID, String evaluationName) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_EVALUATION);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		String status = makePOSTRequest(params);
		return status;
	}

	public static void openEvaluation(String courseID, String evalName) {
		System.out.println("Opening evaluation.");
		HashMap<String, Object> params = createParamMap("evaluation_open");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		makePOSTRequest(params);
	}

	// ------------------------[Submission-level methods]-----------------

	public static String createSubmission(SubmissionData submission) throws NotImplementedException{
		throw new NotImplementedException("Not implemented because creating submissions is automatically done");
	}
	
	public static String getSubmissionAsJason(String courseID,
			String evaluationName, String reviewerEmail, String revieweeEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_SUBMISSION_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		params.put(APIServlet.PARAMETER_REVIEWER_EMAIL, reviewerEmail);
		params.put(APIServlet.PARAMETER_REVIEWEE_EMAIL, revieweeEmail);
		String submissionJson = makePOSTRequest(params);
		return submissionJson;
	}

	public static String editSubmission(SubmissionData submission) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_EDIT_SUBMISSION);
		params.put(APIServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(submission));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteSubmission(String courseID,
			String evaluationName, String reviewerEmail, String revieweeEmail)
			throws NotImplementedException {
		throw new NotImplementedException(
				"not implemented yet because submissions do not need to be deleted via the API");
	}

	// --------------------------------[Tfs-level methods]----------

	public static String createTfs(teammates.jdo.TeamFormingSession tfs)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to create Tfs manually");
	}

	public static String getTfsAsJason(String courseID) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_TFS_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editTfs(TfsData tfs) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_EDIT_TFS);
		params.put(APIServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(tfs));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteTfs(String courseId) throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to delete Tfs manually");
	}

	// --------------------------------[TeamProfile-level methods]----------

	public static String createTeamProfile(teammates.jdo.TeamProfile teamProfile)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to create Team profiles manually");
	}
	
	public static String getTeamProfileAsJason(String courseID, String teamName) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_TEAM_PROFILE_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		params.put(APIServlet.PARAMETER_TEAM_NAME, teamName);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editTeamProfile(String originalTeamName,
			TeamProfile teamProfile) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_EDIT_TEAM_PROFILE);
		params.put(APIServlet.PARAMETER_TEAM_NAME, originalTeamName);
		params.put(APIServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(teamProfile));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteTeamProfile(String courseId, String teamName) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_TEAM_PROFILE);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		params.put(APIServlet.PARAMETER_TEAM_NAME, teamName);
		String status = makePOSTRequest(params);
		return status;
	}

	// -----------------------------[TeamFormingLog-level methods]-------------

	public static String createTeamFormingLog(teammates.jdo.TeamFormingLog tfl)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to create TeamFormingLog manually");
	}
	
	public static String getTeamFormingLogAsJason(String courseID) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_GET_TEAM_FORMING_LOG_AS_JSON);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseID);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}
	
	public static String editTeamFormingLog(teammates.jdo.TeamFormingLog tfl)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to edit TeamFormingLog manually");
	}

	public static String deleteTeamFormingLog(String courseId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_TEAM_FORMING_LOG);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		String status = makePOSTRequest(params);
		return status;
	}

	// ==============================[helper methods]=========================
	/**
	 * This method reformats a Json string in the pretty printing format (i.e.
	 * not the default compact format) e.g. to reformat a Json string whose
	 * formatting was lost during HTTP encoding
	 * 
	 * @param unformattedJason
	 * @param typeOfObject
	 * @return unformattedJason reformatted in pretty printing format
	 */
	public static String reformatJasonString(String unformattedJason,
			Type typeOfObject) {
		Object obj = Common.getTeammatesGson().fromJson(unformattedJason,
				typeOfObject);
		return Common.getTeammatesGson().toJson(obj);
	}

	private static HashMap<String, Object> createParamMap(String string) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("action", string);

		// API Authentication
		map.put("tm_auth", Config.inst().API_AUTH_CODE);

		return map;
	}

	/**
	 * Sends data to server and returns the response
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static String makePOSTRequest(HashMap<String, Object> map) {
		String returnValue = null;
		try {
			StringBuilder dataStringBuilder = new StringBuilder();
			for (Map.Entry<String, Object> e : map.entrySet()) {
				dataStringBuilder.append(URLEncoder.encode(e.getKey(), "UTF-8")
						+ "="
						+ URLEncoder.encode(e.getValue().toString(), "UTF-8")
						+ "&");
			}
			String data = dataStringBuilder.toString();

			// http://teammates/api
			URL url = new URL(Config.inst().TEAMMATES_URL + "api");
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
			returnValue = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	
	
	
	
	
	
	
	// =============================Old API==============================
	
		
	/**
	 * Clean up all tables. Except for Coordinator table.
	 * 
	 * @throws EntityDoesNotExistException
	 */
	@Deprecated
	public static void cleanup() throws EntityDoesNotExistException {
		cleanupByCoordinator();

	}

	/**
	 * Clean up everything related to the coordinator
	 * 
	 * @throws EntityDoesNotExistException
	 * 
	 * @deprecated instead of this, delete coord and recreate.
	 */
	@Deprecated
	public static void cleanupByCoordinator()
			throws EntityDoesNotExistException {
		log.info("Clean up by coordinator");
		cleanupByCoordinator(Config.inst().TEAMMATES_COORD_ID);
	}

	/*
	 * @deprecated instead of this, delete course and recreate.
	 */
	@Deprecated
	public static void cleanupCourse(String courseId) {
		System.out.println("TMAPI.cleanupCourse() courseID = " + courseId);
		HashMap<String, Object> params = createParamMap("cleanup_course");
		params.put("course_id", courseId);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void createCourse(teammates.testing.object.Course course) {
		createCourse(course, "teammates.coord");
	}

	@Deprecated
	public static void createCourse(teammates.testing.object.Course course,
			String coordId) {
		System.out.println("TMAPI Creating course: " + course.courseId);

		HashMap<String, Object> params = createParamMap("course_add");
		params.put("google_id", coordId);
		params.put("course", course.toJSON());
		makePOSTRequest(params);
	}

	@Deprecated
	public static void createEvaluation(Evaluation eval) {
		System.out.println("Creating evaluation.");
		HashMap<String, Object> params = createParamMap("evaluation_add");
		params.put("evaluation", eval.toJSON());
		makePOSTRequest(params);

	}

	@Deprecated
	public static void disableEmail() {
		System.out.println("Disable sending email. ");
		HashMap<String, Object> params = createParamMap("disable_email");
		makePOSTRequest(params);

	}

	@Deprecated
	public static void enableEmail() {
		System.out.println("Enable sending email. ");
		HashMap<String, Object> params = createParamMap("enable_email");
		makePOSTRequest(params);

	}

	public static void openTeamFormingSession(String courseID) {
		System.out.println("Opening team forming session.");

		HashMap<String, Object> params = createParamMap("teamformingsession_open");
		params.put("course_id", courseID);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void createProfileOfExistingTeams(String courseId,
			String courseName, ArrayList<String> teams) {
		System.out.println("Creating profiles of existing teams.");
		String teamProfile = "Please enter your team profile here.";

		for (int i = 0; i < teams.size(); i++) {
			HashMap<String, Object> params = createParamMap("createteamprofiles");
			params.put("course_id", courseId);
			params.put("course_name", courseName);
			params.put("team_name", teams.get(i));
			params.put("team_profile", teamProfile);
			makePOSTRequest(params);
		}
	}

	@Deprecated
	public static void createTeamFormingSession(TeamFormingSession teamForming) {
		System.out.println("Creating team forming session.");
		HashMap<String, Object> params = createParamMap("teamformingsession_add");
		params.put("teamformingsession", teamForming.toJSON());
		makePOSTRequest(params);
	}

	@Deprecated
	public static void closeEvaluation(String courseID, String evalName) {
		System.out.println("Closing evaluation.");

		HashMap<String, Object> params = createParamMap("evaluation_close");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		makePOSTRequest(params);
	}
	/**
	 *@deprecated use persistDataBundle method instead 
	 */
	@Deprecated
	public static void enrollStudents(String courseId, List<Student> students) {
		System.out.println("Enrolling students.");

		HashMap<String, Object> params = createParamMap("enroll_students");

		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		String json_students = gson.toJson(students, listType);
		params.put("course_id", courseId);
		params.put("students", json_students);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void registerStudents(String courseId, List<Student> students) {
		System.out.println("Register students.");

		HashMap<String, Object> params = createParamMap("register_students");

		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		String json_students = gson.toJson(students, listType);
		params.put("course_id", courseId);
		params.put("students", json_students);
		makePOSTRequest(params);

	}

	@Deprecated
	public static void publishEvaluation(String courseID, String evalName) {
		HashMap<String, Object> params = createParamMap("evaluation_publish");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void unpublishEvaluation(String courseID, String evalName) {
		HashMap<String, Object> params = createParamMap("evaluation_unpublish");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void activateAutomatedReminder() {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_SYSTEM_ACTIVATE_AUTOMATED_REMINDER);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void studentsJoinCourse(List<Student> students,
			String courseId) {
		// Go into database and fill feedback from this student.
		System.out.println("Joining course for students.");

		HashMap<String, Object> params = createParamMap("students_join_course");
		params.put("course_id", courseId);
		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		params.put("students", gson.toJson(students, listType));
		makePOSTRequest(params);
	}

	/**
	 * Submit feedbacks for a particular student
	 * 
	 * @prerequisite Evaluation must be open.
	 */
	@Deprecated
	public static void studentSubmitFeedbacks(Student student, String courseId,
			String evaluationName) {
		// Go into database and fill feedback from this student.
		System.out.println("Submitting feedback for student " + student.email);

		HashMap<String, Object> params = createParamMap("student_submit_feedbacks");
		params.put("course_id", courseId);
		params.put("evaluation_name", evaluationName);
		params.put("student_email", student.email);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void updateBumpRatio() {
		// Go into database and fill feedback from this student.
		System.out.println("Update bump ratio");

		HashMap<String, Object> params = createParamMap("update_bump_ratio");
		makePOSTRequest(params);
	}

	@Deprecated
	public static void studentsSubmitFeedbacks(List<Student> students,
			String courseId, String evaluationName) {
		for (Student s : students) {
			studentSubmitFeedbacks(s, courseId, evaluationName);
		}
	}

	/**
	 * New Submission Function for Testing BumpRatio:
	 * 
	 * @author xialin
	 * 
	 **/
	@Deprecated
	public static void studentsSubmitDynamicFeedbacks(List<Student> students,
			String courseId, String evaluationName, String[] submissionPoints)
			throws IOException {
		System.out
				.println("submit dynamic feedbacks:\n\tstudents==>"
						+ students.toString() + "\n\tcourseID==>" + courseId
						+ "\n\tevaluationName==>" + evaluationName
						+ "\n\tsubmissionPoints==>"
						+ Arrays.toString(submissionPoints));
		int i = 0;
		for (Student s : students) {
			String points = getSubmissionPoints(submissionPoints[i]);
			String studentBumpRatio = calculateStudentBumpRatio(points);
			studentSubmitDynamicFeedbacks(s, courseId, evaluationName, points,
					studentBumpRatio);
			i++;
		}

	}

	@Deprecated
	public static void studentSubmitDynamicFeedbacks(Student student,
			String courseId, String evaluationName, String points,
			String studentBumpRatio) {

		HashMap<String, Object> params = createParamMap("student_submit_dynamic_feedbacks");
		params.put("course_id", courseId);
		params.put("evaluation_name", evaluationName);
		params.put("student_email", student.email);
		params.put("team_name", student.teamName);
		params.put("submission_points", points);
		params.put("bumpRatio", studentBumpRatio);

		makePOSTRequest(params);
	}

	@Deprecated
	public static String calculateStudentBumpRatio(String points) {
		String[] pointArray = points.split(", ");
		int[] pointsPassed = new int[pointArray.length];

		int totalPoints = 0;

		for (int i = 0; i < pointArray.length; i++) {
			pointsPassed[i] = Integer.valueOf(pointArray[i]);
		}

		for (int i = 0; i < pointsPassed.length; i++) {
			// Exclude unsure and unfilled entries
			if (!((pointsPassed[i] == -999) || (pointsPassed[i] == -101))) {
				totalPoints = totalPoints + pointsPassed[i];
			}
		}
		float bumpRatio = (float) ((pointsPassed.length * 100.0) / (float) totalPoints);
		return String.valueOf(bumpRatio);
	}

	/**
	 * Evaluation Points Calculation API
	 * 
	 * @param
	 * @author xialin Data structure:
	 *         "original: 100, 100, 100; normalized: 100, 100, 100; claimed: 100; perceived: 100; claimedCoord: 100"
	 **/
	// index 0: original
	@Deprecated
	public static String getSubmissionPoints(String submission) {

		String original = submission.split("; ")[0];// "original: 100, 100, 100"
		String points = original.substring(SUBMISSION_DATA_TAG_ORIGINAL
				.length());// "100, 100, 100"
		return points;
	}

	// index 1: normalized
	@Deprecated
	public static List<String> coordGetPointsToOthersTwoLines(
			String[] submissionPoints, int personIndex) {

		String submission = submissionPoints[personIndex];
		String normalized = submission.split("; ")[1];
		normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED
				.length());
		String[] pointArray = normalized.split(", ");

		// remove self evaluation point:
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < pointArray.length; i++) {
			String point = pointArray[i];
			if (point.length() > 12)// e.g. Equal Share + 20%
				point = point.substring(0, 11) + "\n" + point.substring(12);
			list.add(point);
		}

		return list;
	}

	// point list 2nd format
	@Deprecated
	public static List<String> coordGetPointsToOthersOneLine(
			String[] submissionPoints, int personIndex) {

		String submission = submissionPoints[personIndex];
		String normalized = submission.split("; ")[1];
		normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED
				.length());
		String[] pointArray = normalized.split(", ");

		// remove self evaluation point:
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < pointArray.length; i++) {
			String point = pointArray[i];
			if (point.length() > 12)// e.g. Equal Share + 20%
				point = point.substring(0, 11) + " " + point.substring(12);
			list.add(point);
		}

		return list;
	}

	@Deprecated
	public static List<String> coordGetPointsFromOthersTwoLines(Scenario sc,
			int personIndex) {

		List<String> list = new ArrayList<String>();
		String[] submissionPoints = sc.submissionPoints;
		String teamName = sc.students.get(personIndex).teamName;
		int start = 0;
		int end = 0;
		boolean started = false;
		for (int i = 0; i < sc.students.size(); i++) {
			if (sc.students.get(i).teamName.equalsIgnoreCase(teamName)) {
				if (!started) {
					started = true;
					start = i;
					end = i;
				} else {
					end++;
				}
			}
		}

		for (int i = start; i <= end; i++) {
			// remove self evaluation:
			String submission = submissionPoints[i];
			String normalized = submission.split("; ")[1];
			normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED
					.length());
			String[] pointArray = normalized.split(", ");
			String point = pointArray[personIndex - start];
			if (point.length() > 12)// e.g. Equal Share + 20%
				point = point.substring(0, 11) + "\n" + point.substring(12);
			list.add(point);
		}

		return list;
	}

	// 2nd format
	@Deprecated
	public static List<String> coordGetPointsFromOthersOneLine(Scenario sc,
			int personIndex) {

		List<String> list = new ArrayList<String>();
		String[] submissionPoints = sc.submissionPoints;
		String teamName = sc.students.get(personIndex).teamName;
		int start = 0;
		int end = 0;
		boolean started = false;
		for (int i = 0; i < sc.students.size(); i++) {
			if (sc.students.get(i).teamName.equalsIgnoreCase(teamName)) {
				if (!started) {
					started = true;
					start = i;
					end = i;
				} else {
					end++;
				}
			}
		}

		for (int i = start; i <= end; i++) {
			// remove self evaluation:
			String submission = submissionPoints[i];
			String normalized = submission.split("; ")[1];
			normalized = normalized.substring(SUBMISSION_DATA_TAG_NORMALIZED
					.length());
			String[] pointArray = normalized.split(", ");
			String point = pointArray[personIndex - start];
			if (point.length() > 12)// e.g. Equal Share + 20%
				point = point.substring(0, 11) + " " + point.substring(12);
			list.add(point);
		}

		return list;
	}

	// index 2: claimed
	@Deprecated
	public static String studentGetClaimedPoints(String[] submissionPoints,
			int personIndex) {
		// student should see his/her original submission point:
		String submission = submissionPoints[personIndex];
		String claimed = submission.split("; ")[2];
		claimed = claimed.substring(SUBMISSION_DATA_TAG_CLAIMED.length());
		return claimed;
	}

	// index 3: perceived
	@Deprecated
	public static String studentGetPerceivedPoints(String[] submissionPoints,
			int personIndex) {
		// two normalization steps involved:
		String submission = submissionPoints[personIndex];
		String perceived = submission.split("; ")[3];
		perceived = perceived.substring(SUBMISSION_DATA_TAG_PERCEIVED.length());
		return perceived;
	}

	// index 4: claimedCoord
	@Deprecated
	public static String coordGetClaimedPoints(String[] submissionPoints,
			int personIndex) {
		String submission = submissionPoints[personIndex];
		String claimedCoord = submission.split("; ")[4];
		claimedCoord = claimedCoord.substring(SUBMISSION_DATA_TAG_CLAIMEDCOORD
				.length());
		return claimedCoord;
	}

	// index 5: perceivedCoord
	@Deprecated
	public static String coordGetPerceivedPoints(String[] submissionPoints,
			int personIndex) {
		String submission = submissionPoints[personIndex];
		String perceived = submission.split("; ")[5];
		perceived = perceived.substring(SUBMISSION_DATA_TAG_PERCEIVEDCOORD
				.length());
		return perceived;
	}

	// index 6: perceivedCoord - claimedCoord (diff)
	@Deprecated
	public static String coordGetPointDifference(String[] submissionPoints,
			int personIndex) {
		String claimed = coordGetClaimedPoints(submissionPoints, personIndex);
		String perceived = coordGetPerceivedPoints(submissionPoints,
				personIndex);

		if (claimed.equals("N/A") || perceived.equals("N/A")) {
			return "N/A";
		} else {
			String submission = submissionPoints[personIndex];
			String difference = submission.split("; ")[6];
			difference = difference.substring(SUBMISSION_DATA_TAG_DIFFERENCE
					.length());
			return difference;
		}
	}

	// index 7: bump ratio
	@Deprecated
	public static String coordGetSubmissionBumpRatio(String[] submissionPoints,
			int personIndex) {
		String submission = submissionPoints[personIndex];
		String bumpratio = submission.split("; ")[7];
		bumpratio = bumpratio.substring(SUBMISSION_DATA_TAG_BUMPRATIO.length());
		return bumpratio;
	}

	// Oct 12 end--------------------------------

	/**
	 * Mail Stress Testing
	 * 
	 * @param account
	 * @param size
	 * @author wangsha
	 */
	@Deprecated
	public static void mailStressTesting(String account, int size) {
		System.out.println("Mail testing " + account + ", size:" + size);

		HashMap<String, Object> params = createParamMap("email_stress_testing");
		params.put("account", account);
		params.put("size", size);
		makePOSTRequest(params);

	}

	// ---------------------------------
	// PRIVATE HELPER FUNCTIONS
	// ---------------------------------

	@Deprecated
	public static void deleteCourseByIdNonCascade(String courseId) {
		System.out.println("TMAPI deleting course (non cascade): " + courseId);
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE);
		params.put(APIServlet.PARAMETER_COURSE_ID, courseId);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void deleteCourseByIdCascade(String courseId) {
		deleteCourseByIdNonCascade(courseId);

	}

	@Deprecated
	public static void deleteCoordByIdNonCascading(String coordId) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_DELETE_COORD_NON_CASCADE);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		makePOSTRequest(params);
	}

	@Deprecated
	public static void createCoord(String coordId, String coordName,
			String coordEmail) {
		HashMap<String, Object> params = createParamMap(APIServlet.OPERATION_CREATE_COORD);
		params.put(APIServlet.PARAMETER_COORD_ID, coordId);
		params.put(APIServlet.PARAMETER_COORD_NAME, coordName);
		params.put(APIServlet.PARAMETER_COORD_EMAIL, coordEmail);
		makePOSTRequest(params);
	}


}
