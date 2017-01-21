package teammates.test.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Sanitizer;
import teammates.logic.backdoor.BackDoorOperation;

import com.google.gson.reflect.TypeToken;

/**
 * Used to access the datastore without going through the UI. The main use of
 * this class is for the test suite to prepare test data.<br>
 * It works only if the test.backdoor.key in test.properties matches the
 * app.backdoor.key in build.properties of the deployed app.<br>
 * Using this mechanism, we can limit back door access to only the person who
 * deployed the application.
 */
public final class BackDoor {
    
    private BackDoor() {
        //utility class
    }

    /**
     * Persists given data. If given entities already exist in the data store,
     * they will be overwritten.
     */
    public static String restoreDataBundle(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_PERSIST_DATABUNDLE);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }
    
    /**
     * Removes given data. If given entities have already been deleted,
     * it fails silently.
     */
    public static String removeDataBundle(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_REMOVE_DATABUNDLE);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }
    
    /**
     * Removes and restores given data.
     */
    public static String removeAndRestoreDataBundle(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_REMOVE_AND_RESTORE_DATABUNDLE);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * This create documents for entities through back door.
     */
    public static String putDocuments(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_PUT_DOCUMENTS);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }

    public static String createAccount(AccountAttributes account) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.accounts.put(account.googleId, account);
        return restoreDataBundle(dataBundle);
    }
    
    public static AccountAttributes getAccount(String googleId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_ACCOUNT_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        String accountJsonString = makePostRequest(params);
        return JsonUtils.fromJson(accountJsonString, AccountAttributes.class);
    }
    
    public static StudentProfileAttributes getStudentProfile(String googleId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_STUDENTPROFILE_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        String studentProfileJsonString = makePostRequest(params);
        return JsonUtils.fromJson(studentProfileJsonString, StudentProfileAttributes.class);
    }
    
    public static boolean getWhetherPictureIsPresentInGcs(String pictureKey) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_IS_PICTURE_PRESENT_IN_GCS);
        params.put(BackDoorOperation.PARAMETER_PICTURE_KEY, pictureKey);
        return Boolean.parseBoolean(makePostRequest(params));
    }

    public static String uploadAndUpdateStudentProfilePicture(String googleId, String pictureDataJsonString) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_STUDENT_PROFILE_PICTURE);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        params.put(BackDoorOperation.PARAMETER_PICTURE_DATA, pictureDataJsonString);
        return makePostRequest(params);
    }

    public static String deleteAccount(String googleId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_ACCOUNT);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        return makePostRequest(params);
    }
    
    public static String createInstructor(InstructorAttributes instructor) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.instructors.put(instructor.googleId, instructor);
        return restoreDataBundle(dataBundle);
    }

    public static InstructorAttributes getInstructorByGoogleId(String instructorId, String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_ID, instructorId);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String instructorJsonString = makePostRequest(params);
        return JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
    }
    
    public static InstructorAttributes getInstructorByEmail(String instructorEmail, String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL, instructorEmail);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String instructorJsonString = makePostRequest(params);
        return JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
    }
    
    public static String getEncryptedKeyForInstructor(String courseId, String instructorEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_ENCRYPTED_KEY_FOR_INSTRUCTOR);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL, instructorEmail);
        return makePostRequest(params);
    }

    public static String deleteInstructor(String courseId, String instructorEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_INSTRUCTOR);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL, instructorEmail);
        return makePostRequest(params);
    }

    public static String createCourse(CourseAttributes course) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.courses.put("dummy-key", course);
        return restoreDataBundle(dataBundle);
    }

    public static CourseAttributes getCourse(String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_COURSE_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String courseJsonString = makePostRequest(params);
        return JsonUtils.fromJson(courseJsonString, CourseAttributes.class);
    }
    
    public static String deleteCourse(String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_COURSE);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        return makePostRequest(params);
    }

    public static String createStudent(StudentAttributes student) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.students.put("dummy-key", student);
        return restoreDataBundle(dataBundle);
    }

    public static StudentAttributes getStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_STUDENT_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, studentEmail);
        String studentJson = makePostRequest(params);
        return JsonUtils.fromJson(studentJson, StudentAttributes.class);
    }

    public static String getEncryptedKeyForStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_ENCRYPTED_KEY_FOR_STUDENT);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, studentEmail);
        return makePostRequest(params);
    }

    public static String editStudent(String originalEmail, StudentAttributes student) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_STUDENT);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, originalEmail);
        params.put(BackDoorOperation.PARAMETER_JSON_STRING, JsonUtils.toJson(student));
        return makePostRequest(params);
    }

    public static String deleteStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_STUDENT);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, studentEmail);
        return makePostRequest(params);
    }

    public static FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_SESSION_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String feedbackSessionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackSessionJson, FeedbackSessionAttributes.class);
    }
    
    public static String editFeedbackSession(FeedbackSessionAttributes updatedFeedbackSession) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_FEEDBACK_SESSION);
        params.put(BackDoorOperation.PARAMETER_JSON_STRING, JsonUtils.toJson(updatedFeedbackSession));
        return makePostRequest(params);
    }
    
    public static String deleteFeedbackSession(String feedbackSessionName, String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_FEEDBACK_SESSION);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        return makePostRequest(params);
    }
    
    public static FeedbackQuestionAttributes getFeedbackQuestion(String courseId, String feedbackSessionName,
                                                                 int qnNumber) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_QUESTION_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_NUMBER, String.valueOf(qnNumber));
        String feedbackQuestionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
    }
    
    public static FeedbackQuestionAttributes getFeedbackQuestion(String questionId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, questionId);
        String feedbackQuestionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
    }
    
    public static String editFeedbackQuestion(FeedbackQuestionAttributes updatedFeedbackQuestion) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_FEEDBACK_QUESTION);
        params.put(BackDoorOperation.PARAMETER_JSON_STRING, JsonUtils.toJson(updatedFeedbackQuestion));
        return makePostRequest(params);
    }

    public static String deleteFeedbackQuestion(String questionId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_FEEDBACK_QUESTION);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, questionId);
        return makePostRequest(params);
    }

    public static String createFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.feedbackResponses.put("dummy-key", feedbackResponse);
        return restoreDataBundle(dataBundle);
    }
    
    public static FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId, String giverEmail,
                                                                 String recipient) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, feedbackQuestionId);
        params.put(BackDoorOperation.PARAMETER_GIVER_EMAIL, giverEmail);
        params.put(BackDoorOperation.PARAMETER_RECIPIENT, recipient);
        String feedbackResponseJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackResponseJson, FeedbackResponseAttributes.class);
    }
    
    public static List<FeedbackResponseAttributes>
            getFeedbackResponsesForReceiverForCourse(String courseId, String recipientEmail) {
        Map<String, String> params =
                createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_RESPONSES_FOR_RECEIVER_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_RECIPIENT, recipientEmail);
        String feedbackResponsesJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackResponsesJson,
                                  new TypeToken<List<FeedbackResponseAttributes>>(){}.getType());
    }
    
    public static List<FeedbackResponseAttributes>
            getFeedbackResponsesFromGiverForCourse(String courseId, String giverEmail) {
        Map<String, String> params =
                createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_RESPONSES_FOR_GIVER_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_GIVER_EMAIL, giverEmail);
        String feedbackResponsesJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackResponsesJson,
                                  new TypeToken<List<FeedbackResponseAttributes>>(){}.getType());
    }

    public static String deleteFeedbackResponse(String feedbackQuestionId, String giverEmail, String recipient) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_FEEDBACK_RESPONSE);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, feedbackQuestionId);
        params.put(BackDoorOperation.PARAMETER_GIVER_EMAIL, giverEmail);
        params.put(BackDoorOperation.PARAMETER_RECIPIENT, recipient);
        return makePostRequest(params);
    }

    private static Map<String, String> createParamMap(BackDoorOperation operation) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(BackDoorOperation.PARAMETER_BACKDOOR_OPERATION, operation.toString());

        // For authentication
        map.put(BackDoorOperation.PARAMETER_BACKDOOR_KEY, TestProperties.BACKDOOR_KEY);

        return map;
    }

    private static String makePostRequest(Map<String, String> map) {
        try {
            String paramString = encodeParameters(map);
            String urlString = TestProperties.TEAMMATES_URL + Const.ActionURIs.BACKDOOR;
            URLConnection conn = getConnectionToUrl(urlString);
            sendRequest(paramString, conn);
            return readResponse(conn);
        } catch (Exception e) {
            return TeammatesException.toStringWithStackTrace(e);
        }
    }

    private static String readResponse(URLConnection conn) throws IOException {
        conn.setReadTimeout(10000);
        InputStreamReader isr = new InputStreamReader(conn.getInputStream(), Const.SystemParams.ENCODING);
        BufferedReader rd = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        return sb.toString();
    }

    private static void sendRequest(String paramString, URLConnection conn) throws IOException {
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), Const.SystemParams.ENCODING);
        wr.write(paramString);
        wr.flush();
        wr.close();
    }

    private static URLConnection getConnectionToUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        return conn;
    }

    private static String encodeParameters(Map<String, String> map) {
        StringBuilder dataStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> e : map.entrySet()) {
            dataStringBuilder.append(e.getKey() + "=" + Sanitizer.sanitizeForUri(e.getValue().toString()) + "&");
        }
        return dataStringBuilder.toString();
    }

}
