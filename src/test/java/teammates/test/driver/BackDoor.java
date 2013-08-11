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

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.NotImplementedException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
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

	private static final int RETRY_DELAY_IN_MILLISECONDS = 5000;

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
	 * Persists given data. If given entities already exist in the data store,
	 * they will be overwritten.
	 */
	public static String restoreDataBundle(DataBundle dataBundle) {
		String json = Utils.getTeammatesGson().toJson(dataBundle);
		return persistNewDataBundle(json);
	}

	/**
	 * Deletes instructors contained in the jsonString
	 * 
	 * @param jsonString
	 */
	public static void deleteInstructors(String jsonString) {
		Gson gson = Utils.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		deleteInstructors(data);
	}

	private static void deleteInstructors(DataBundle data) {
		HashMap<String, InstructorAttributes> instructors = data.instructors;
		for (InstructorAttributes instructor : instructors.values()) {
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
		Gson gson = Utils.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		HashMap<String, CourseAttributes> courses = data.courses;
		for (CourseAttributes course : courses.values()) {
			deleteCourse(course.id);
		}
	}
	
	/**
	 * Deletes FEEDBACK SESSIONS contained in the jsonString
	 * 
	 * This should recursively delete all FEEDBACK QUESIONS AND RESPONSES related to the session.
	 * 
	 * @param jsonString
	 */
	public static void deleteFeedbackSessions(DataBundle data) {
		HashMap<String, FeedbackSessionAttributes> feedbackSessions = data.feedbackSessions;
		for (FeedbackSessionAttributes feedbackSession : feedbackSessions.values()) {
			deleteFeedbackSession(
					feedbackSession.feedbackSessionName,
					feedbackSession.courseId);
		}
	}
	
	//====================================================================================
	
	@SuppressWarnings("unused")
	private void ____ACCOUNT_level_methods______________________________() {
	}
	
	public static String createAccount(AccountAttributes account) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.accounts.put(account.googleId, account);
		return persistNewDataBundle(Utils.getTeammatesGson()
				.toJson(dataBundle));
	}
	
	public static AccountAttributes getAccount(String googleId) {
		return Utils.getTeammatesGson().fromJson(getAccountAsJson(googleId), AccountAttributes.class);
	}
	
	/**
	 * If object not found in the first try, it will retry once more after a delay.
	 */
	public static AccountAttributes getAccountWithRetry(String googleId) {
		AccountAttributes a = getAccount(googleId);
		if(a == null){
			ThreadHelper.waitFor(RETRY_DELAY_IN_MILLISECONDS);
			a = getAccount(googleId);
		}
		return a;
	}
	
	public static String getAccountAsJson(String googleId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_ACCOUNT_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_GOOGLE_ID, googleId);
		String instructorJsonString = makePOSTRequest(params);
		return instructorJsonString;
	}

	public static String editAccount(AccountAttributes account) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_ACCOUNT);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Utils
				.getTeammatesGson().toJson(account));
		String status = makePOSTRequest(params);
		return status;
	}

	public static String deleteAccount(String googleId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_ACCOUNT);
		params.put(BackDoorServlet.PARAMETER_GOOGLE_ID, googleId);
		String status = makePOSTRequest(params);
		return status;
	}
	
	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_level_methods______________________________() {
	}

	public static String createInstructor(InstructorAttributes instructor) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.instructors.put(instructor.googleId, instructor);
		return persistNewDataBundle(Utils.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getInstructorAsJson(String instructorId, String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_INSTRUCTOR_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_INSTRUCTOR_ID, instructorId);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String instructorJsonString = makePOSTRequest(params);
		return instructorJsonString;
	}
	
	public static InstructorAttributes getInstructor(String instructorId, String courseId) {
		String json = getInstructorAsJson(instructorId, courseId);
		return Utils.getTeammatesGson().fromJson(json, InstructorAttributes.class);
	}

	public static String editInstructor(InstructorAttributes instructor)
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
		if (StringHelper.isWhiteSpace(courseString)) {
			return coursesArray;
		}
		coursesArray = courseString.trim().split(" ");
		Arrays.sort(coursesArray);
		return coursesArray;
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods______________________________() {
	}

	public static String createCourse(CourseAttributes course) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.courses.put("dummy-key", course);
		return persistNewDataBundle(Utils.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getCourseAsJson(String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_COURSE_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String courseJsonString = makePOSTRequest(params);
		return courseJsonString;
	}
	
	public static CourseAttributes getCourse(String courseId) {
		return Utils.getTeammatesGson().fromJson(getCourseAsJson(courseId), CourseAttributes.class);
	}
	
	/**
	 * Checks existence with a bias for non existence. If object found in the
	 * first try, it will retry once more after a delay.
	 */
	public static boolean isCourseNonExistent(String courseId) {
		CourseAttributes c = getCourse(courseId);
		if(c != null){
			ThreadHelper.waitFor(RETRY_DELAY_IN_MILLISECONDS);
			c = getCourse(courseId);
		}
		return c == null;
	}

	public static String editCourse(CourseAttributes course)
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

	public static String createStudent(StudentAttributes student) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.students.put("dummy-key", student);
		return persistNewDataBundle(Utils.getTeammatesGson()
				.toJson(dataBundle));
	}

	public static String getStudentAsJson(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_STUDENT_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String studentJson = makePOSTRequest(params);
		return studentJson;
	}
	
	public static StudentAttributes getStudent(String courseId, String studentEmail) {
		String studentJson = getStudentAsJson(courseId, studentEmail);
		return Utils.getTeammatesGson().fromJson(studentJson, StudentAttributes.class);
	}

	public static String getKeyForStudent(String courseId, String studentEmail) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_KEY_FOR_STUDENT);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, studentEmail);
		String regKey = makePOSTRequest(params);
		return regKey;

	}

	public static String editStudent(String originalEmail, StudentAttributes student) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_STUDENT);
		params.put(BackDoorServlet.PARAMETER_STUDENT_EMAIL, originalEmail);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Utils
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

	public static String createEvaluation(EvaluationAttributes evaluation) {
		DataBundle dataBundle = new DataBundle();
		dataBundle.evaluations.put("dummy-key", evaluation);
		return persistNewDataBundle(Utils.getTeammatesGson()
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
	
	/**
	 * Checks existence with a bias for non existence. If object found in the
	 * first try, it will retry once more after a delay.
	 */
	public static boolean isEvaluationNonExistent(String courseID, String evaluationName) {
		EvaluationAttributes e = getEvaluation(courseID, evaluationName);
		if(e != null){
			ThreadHelper.waitFor(RETRY_DELAY_IN_MILLISECONDS);
			e = getEvaluation(courseID, evaluationName);
		}
		return (e == null) ;
	}
	
	public static EvaluationAttributes getEvaluation(String courseID,
			String evaluationName) {
		String jsonString = getEvaluationAsJson(courseID, evaluationName);
		return Utils.getTeammatesGson().fromJson(jsonString, EvaluationAttributes.class);
	}

	public static String editEvaluation(EvaluationAttributes evaluation) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_EVALUATION);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Utils
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

	public static String createSubmission(SubmissionAttributes submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because creating submissions is automatically done");
	}
	
	public static SubmissionAttributes getSubmission(String courseID,
			String evaluationName, String reviewerEmail, String revieweeEmail) {
		return Utils.getTeammatesGson()
				.fromJson(
						getSubmissionAsJson(courseID, evaluationName, reviewerEmail, revieweeEmail),
						SubmissionAttributes.class);
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

	public static String editSubmission(SubmissionAttributes submission) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_SUBMISSION);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Utils
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
	private void ____FEEDBACK_SESSION_level_methods______________________________() {
	}

	public static FeedbackSessionAttributes getFeedbackSession(String courseID,
			String feedbackSessionName) {
		String jsonString = getFeedbackSessionAsJson(feedbackSessionName, courseID);
		return Utils.getTeammatesGson().fromJson(jsonString, FeedbackSessionAttributes.class);
	}
	
	public static String getFeedbackSessionAsJson(String feedbackSessionName,
			String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_FEEDBACK_SESSION_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String feedbackSessionJson = makePOSTRequest(params);
		return feedbackSessionJson;
	}
	
	public static String editFeedbackSession(FeedbackSessionAttributes updatedFeedbackSession) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_EDIT_FEEDBACK_SESSION);
		params.put(BackDoorServlet.PARAMETER_JASON_STRING, Utils
				.getTeammatesGson().toJson(updatedFeedbackSession));
		String status = makePOSTRequest(params);
		return status;
	}
	
	public static String deleteFeedbackSession(String feedbackSessionName,
			String courseId) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_DELETE_FEEDBACK_SESSION);
		params.put(BackDoorServlet.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		String status = makePOSTRequest(params);
		return status;
	}
	
	@SuppressWarnings("unused")
	private void ____FEEDBACK_QUESTION_level_methods______________________________() {
	}

	public static FeedbackQuestionAttributes getFeedbackQuestion(String courseID,
			String feedbackSessionName, int qnNumber) {
		String jsonString = getFeedbackQuestionAsJson(feedbackSessionName, courseID, qnNumber);
		Utils.getLogger().info(jsonString);
		return Utils.getTeammatesGson().fromJson(jsonString, FeedbackQuestionAttributes.class);
	}
	
	public static String getFeedbackQuestionAsJson(String feedbackSessionName,
			String courseId, int qnNumber) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_FEEDBACK_QUESTION_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
		params.put(BackDoorServlet.PARAMETER_COURSE_ID, courseId);
		params.put(BackDoorServlet.PARAMETER_FEEDBACK_QUESTION_NUMBER, qnNumber);
		String feedbackQuestionJson = makePOSTRequest(params);
		return feedbackQuestionJson;
	}
	
	@SuppressWarnings("unused")
	private void ____FEEDBACK_RESPONSE_level_methods______________________________() {
	}
	
	public static FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId,
			String giverEmail, String recipient) {
		String jsonString = getFeedbackResponseAsJson(feedbackQuestionId, giverEmail, recipient);
		Utils.getLogger().info(jsonString);
		return Utils.getTeammatesGson().fromJson(jsonString, FeedbackResponseAttributes.class);
	}

	public static String getFeedbackResponseAsJson(String feedbackQuestionId,
			String giverEmail, String recipient) {
		HashMap<String, Object> params = createParamMap(BackDoorServlet.OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON);
		params.put(BackDoorServlet.PARAMETER_FEEDBACK_QUESTION_ID, feedbackQuestionId);
		params.put(BackDoorServlet.PARAMETER_GIVER_EMAIL, giverEmail);
		params.put(BackDoorServlet.PARAMETER_RECIPIENT, recipient);
		String feedbackResponseJson = makePOSTRequest(params);
		return feedbackResponseJson;
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
					+ Const.ActionURIs.BACKDOOR;
			URLConnection conn = getConnectionToUrl(urlString);
			sendRequest(paramString, conn);
			return readResponse(conn);
		} catch (Exception e) {
			return TeammatesException.toStringWithStackTrace(e);
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
