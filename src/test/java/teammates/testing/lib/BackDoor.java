package teammates.testing.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import teammates.BackDoorServlet;
import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.NotImplementedException;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.datatransfer.TeamProfileData;
import teammates.datatransfer.TfsData;
import teammates.testing.config.Config;
import teammates.testing.object.Course;

import com.google.gson.Gson;

public class BackDoor {
	private static Logger log = Common.getLogger();
	// --------------------[System-level methods]-----------------------------

	public static String persistNewDataBundle(String dataBundleJason) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_PERSIST_DATABUNDLE);
		params.put(BackDoorServlet.PARAMETER_DATABUNDLE_JSON, dataBundleJason);
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
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_COORD_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COORD_ID, coordId);
		String coordJsonString = makePOSTRequest(params);
		return coordJsonString;
	}

	public static String editCoord(CoordData coord)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because editing coordinators is not currently allowed");
	}

	public static String deleteCoord(String coordId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_COORD);
		params.put(BackDoorServlet.PARAMETER_COORD_ID, coordId);
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

		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_COURSES_BY_COORD);
		params.put(BackDoorServlet.PARAMETER_COORD_ID, coordId);
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

	public static String getCourseAsJason(String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_COURSE_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String courseJsonString = makePOSTRequest(params);
		return courseJsonString;
	}

	public static String editCourse(Course course)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because editing courses is not currently allowed");
	}

	public static String deleteCourse(String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_COURSE);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
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
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_STUDENT_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String studentJson = makePOSTRequest(params);
		return studentJson;
	}
	
	public static String getKeyForStudent(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_KEY_FOR_STUDENT);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String regKey = makePOSTRequest(params);
		return regKey;
		
	}


	public static String editStudent(String originalEmail, StudentData student) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_STUDENT);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, originalEmail);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(student));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteStudent(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_STUDENT);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
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
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_EVALUATION_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		params.put(BackDoorServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editEvaluation(EvaluationData evaluation) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_EVALUATION);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(evaluation));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteEvaluation(String courseID, String evaluationName) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_EVALUATION);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		params.put(BackDoorServlet.PARAMETER_EVALUATION_NAME, evaluationName);
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

	public static String createSubmission(SubmissionData submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because creating submissions is automatically done");
	}

	public static String getSubmissionAsJason(String courseID,
			String evaluationName, String reviewerEmail, String revieweeEmail) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_SUBMISSION_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		params.put(BackDoorServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		params.put(BackDoorServlet.PARAMETER_REVIEWER_EMAIL, reviewerEmail);
		params.put(BackDoorServlet.PARAMETER_REVIEWEE_EMAIL, revieweeEmail);
		String submissionJson = makePOSTRequest(params);
		return submissionJson;
	}

	public static String editSubmission(SubmissionData submission) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_SUBMISSION);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
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

	public static String createTfs(teammates.persistent.TeamFormingSession tfs)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to create Tfs manually");
	}

	public static String getTfsAsJason(String courseID) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_TFS_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editTfs(TfsData tfs) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_TFS);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(tfs));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteTfs(String courseId)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to delete Tfs manually");
	}

	// --------------------------------[TeamProfile-level methods]----------

	public static String createTeamProfile(
			teammates.persistent.TeamProfile teamProfile)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to create Team profiles manually");
	}

	public static String getTeamProfileAsJason(String courseID, String teamName) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_TEAM_PROFILE_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		params.put(BackDoorServlet.PARAMETER_TEAM_NAME, teamName);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editTeamProfile(String originalTeamName,
			TeamProfileData teamProfile) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_TEAM_PROFILE);
		params.put(BackDoorServlet.PARAMETER_TEAM_NAME, originalTeamName);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common.getTeammatesGson()
				.toJson(teamProfile));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteTeamProfile(String courseId, String teamName) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_TEAM_PROFILE);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_TEAM_NAME, teamName);
		String status = makePOSTRequest(params);
		return status;
	}

	// -----------------------------[TeamFormingLog-level methods]-------------

	public static String createTeamFormingLog(
			teammates.persistent.TeamFormingLog tfl)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to create TeamFormingLog manually");
	}

	public static String getTeamFormingLogAsJason(String courseID) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_TEAM_FORMING_LOG_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editTeamFormingLog(
			teammates.persistent.TeamFormingLog tfl)
			throws NotImplementedException {
		throw new NotImplementedException(
				"This method is not implemented because there is no"
						+ " need to edit TeamFormingLog manually");
	}

	public static String deleteTeamFormingLog(String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_TEAM_FORMING_LOG);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
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
			URL url = new URL(Config.inst().TEAMMATES_URL + "backdoor");
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


}
