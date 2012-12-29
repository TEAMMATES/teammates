package teammates.test.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.NotImplementedException;
import teammates.logic.backdoor.BackDoorServlet;

import com.google.gson.Gson;

/**
 * Used to access the datastore without going through the UI. The main use of
 * this class is for the test suite to prepare test data. <br>
 * It works only if the test.backdoor.key in test.properties matches the
 * app.backdoor.key in build.properties of the deployed app. Using this
 * mechanism we can limit back door access to only the person who deployed the
 * application.
 * 
 */
public class BackDoor {

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods______________________________() {
	}

	/**
	 * This persists the given data if no such data already exists in the
	 * datastore.
	 * 
	 * @param dataBundleJason
	 * @return
	 */
	public static String persistNewDataBundle(String dataBundleJason) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_PERSIST_DATABUNDLE);
		params.put(BackDoorServlet.PARAMETER_DATABUNDLE_JSON, dataBundleJason);
		String status = makePOSTRequest(params);
		return status;
	}

	/**
	 * Persists given data. If given entities already exist in the data store,
	 * they will be overwritten.
	 * 
	 * @param dataBundleJason
	 * @return
	 */
	public static String restoreDataBundle(String dataBundleJason) {
		deleteInstructors(dataBundleJason);
		return persistNewDataBundle(dataBundleJason);
	}

	/**
	 * Deletes instructors contained in the jsonString
	 * 
	 * @param jsonString
	 */
	public static void deleteInstructors(String jsonString) {
		Gson gson = Common.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, InstructorData> instructors = data.instructors;
		for (InstructorData instructor : instructors.values()) {
			deleteInstructor(instructor.googleId);
		}
	}
	
	/**
	 * Deletes COURSES contained in the jsonString
	 * 
	 * This should recursively delete all INSTRUCTORS, EVALUATIONS, SUBMISSIONS and STUDENTS related
	 * 
	 * @param jsonString
	 */
	public static void deleteCourses(String jsonString) {
		Gson gson = Common.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, CourseData> courses = data.courses;
		for (CourseData course : courses.values()) {
			deleteCourse(course.id);
		}
	}

	/**
	 * Call the migration BackDoorLogic method
	 */
	public static String createInstructorsFromCourses() {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_CREATE_INSTRUCTORS_FROM_COURSES);
		String status = makePOSTRequest(params);
		return status;
	}
	
	@SuppressWarnings("unused")
	private void ____ACCOUNT_level_methods______________________________() {
	}
	
	public static String createAccount(AccountData account) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.accounts.put(account.googleId, account);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}
	
	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_level_methods______________________________() {
	}

	public static String createInstructor(InstructorData instructor) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.instructors.put(instructor.googleId, instructor);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getInstructorAsJson(String instructorId, String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_INSTRUCTOR_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_INSTRUCTOR_ID, instructorId);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String instructorJsonString = makePOSTRequest(params);
		return instructorJsonString;
	}

	public static String editInstructor(InstructorData instructor)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because editing instructors is not currently allowed");
	}

	public static String deleteInstructor(String instructorId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_INSTRUCTOR);
		params.put(BackDoorServlet.PARAMETER_INSTRUCTOR_ID, instructorId);
		String status = makePOSTRequest(params);
		return status;
	}
	

	

	public static String[] getCoursesByInstructorId(String instructorId) {

		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_COURSES_BY_INSTRUCTOR);
		params.put(BackDoorServlet.PARAMETER_INSTRUCTOR_ID, instructorId);
		String courseString = makePOSTRequest(params);
		String[] coursesArray = {};
		if (Common.isWhiteSpace(courseString)) {
			return coursesArray;
		}
		coursesArray = courseString.trim().split(" ");
		Arrays.sort(coursesArray);
		return coursesArray;
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods______________________________() {
	}

	public static String createCourse(CourseData course) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.courses.put("dummy-key", course);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getCourseAsJson(String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_COURSE_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String courseJsonString = makePOSTRequest(params);
		return courseJsonString;
	}

	public static String editCourse(CourseData course)
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

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods______________________________() {
	}

	public static String createStudent(StudentData student) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.students.put("dummy-key", student);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getStudentAsJson(String courseId, String studentEmail) {
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
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common
				.getTeammatesGson().toJson(student));
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

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods______________________________() {
	}

	public static String createEvaluation(EvaluationData evaluation) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.evaluations.put("dummy-key", evaluation);
		return persistNewDataBundle(Common.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getEvaluationAsJson(String courseID,
			String evaluationName) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_EVALUATION_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseID);
		params.put(BackDoorServlet.PARAMETER_EVALUATION_NAME, evaluationName);
		String evaluationJson = makePOSTRequest(params);
		return evaluationJson;
	}

	public static String editEvaluation(EvaluationData evaluation) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_EVALUATION);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common
				.getTeammatesGson().toJson(evaluation));
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

	@SuppressWarnings("unused")
	private void ____SUBMISSION_level_methods______________________________() {
	}

	public static String createSubmission(SubmissionData submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because creating submissions is automatically done");
	}

	public static String getSubmissionAsJson(String courseID,
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
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Common
				.getTeammatesGson().toJson(submission));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteSubmission(String courseID,
			String evaluationName, String reviewerEmail, String revieweeEmail)
			throws NotImplementedException {
		throw new NotImplementedException(
				"not implemented yet because submissions do not need to be deleted via the API");
	}

	@SuppressWarnings("unused")
	private void ____helper_methods______________________________() {
	}

	private static HashMap<String, Object> createParamMap(String operation) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(BackDoorServlet.PARAMETER_BACKDOOR_OPERATION, operation);

		// For Authentication
		map.put(BackDoorServlet.PARAMETER_BACKDOOR_KEY,
				TestProperties.inst().BACKDOOR_KEY);

		return map;
	}

	private static String makePOSTRequest(HashMap<String, Object> map) {
		try {
			String paramString = encodeParameters(map);
			String urlString = TestProperties.inst().TEAMMATES_URL
					+ Common.PAGE_BACKDOOR;
			URLConnection conn = getConnectionToUrl(urlString);
			sendRequest(paramString, conn);
			return readResponse(conn);
		} catch (Exception e) {
			return Common.stackTraceToString(e);
		}
	}

	private static String readResponse(URLConnection conn) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		return sb.toString();
	}

	private static void sendRequest(String paramString, URLConnection conn)
			throws IOException {
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(paramString);
		wr.flush();
		wr.close();
	}

	private static URLConnection getConnectionToUrl(String urlString)
			throws MalformedURLException, IOException {
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		return conn;
	}

	private static String encodeParameters(HashMap<String, Object> map)
			throws UnsupportedEncodingException {
		StringBuilder dataStringBuilder = new StringBuilder();
		for (Map.Entry<String, Object> e : map.entrySet()) {
			dataStringBuilder.append(URLEncoder.encode(e.getKey(), "UTF-8")
					+ "=" + URLEncoder.encode(e.getValue().toString(), "UTF-8")
					+ "&");
		}
		String data = dataStringBuilder.toString();
		return data;
	}

}
