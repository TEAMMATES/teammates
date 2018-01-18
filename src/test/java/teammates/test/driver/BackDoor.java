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

import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.logic.backdoor.BackDoorOperation;

/**
 * Used to access the datastore without going through the UI.
 *
 * <p>It requires an authentication via "backdoor key" so that
 * the access is limited only to the person who deployed the application.
 */
public final class BackDoor {

    private BackDoor() {
        //utility class
    }

    /**
     * Persists given data into the datastore.
     *
     * <p>If given entities already exist in the data store, they will be overwritten.
     */
    public static String restoreDataBundle(DataBundle dataBundle) {
        removeAdminEmailsFromDataBundle(dataBundle);
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_PERSIST_DATABUNDLE);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Removes given data from the datastore.
     *
     * <p>If given entities have already been deleted, it fails silently.
     */
    public static String removeDataBundle(DataBundle dataBundle) {
        removeAdminEmailsFromDataBundle(dataBundle);
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_REMOVE_DATABUNDLE);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Removes and restores given data into the datastore.
     */
    public static String removeAndRestoreDataBundle(DataBundle dataBundle) {
        removeAdminEmailsFromDataBundle(dataBundle);
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_REMOVE_AND_RESTORE_DATABUNDLE);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Puts searchable documents for entities into the datastore.
     */
    public static String putDocuments(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_PUT_DOCUMENTS);
        params.put(BackDoorOperation.PARAMETER_DATABUNDLE_JSON, dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Persists an account data into the datastore.
     */
    public static String createAccount(AccountAttributes account) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.accounts.put(account.googleId, account);
        return restoreDataBundle(dataBundle);
    }

    /**
     * Gets an account data from the datastore.
     */
    public static AccountAttributes getAccount(String googleId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_ACCOUNT_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        String accountJsonString = makePostRequest(params);
        return JsonUtils.fromJson(accountJsonString, AccountAttributes.class);
    }

    /**
     * Gets a student profile data from the datastore.
     */
    public static StudentProfileAttributes getStudentProfile(String googleId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_STUDENTPROFILE_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        String studentProfileJsonString = makePostRequest(params);
        return JsonUtils.fromJson(studentProfileJsonString, StudentProfileAttributes.class);
    }

    /**
     * Checks if a profile picture with the specified key is present in GCS.
     */
    public static boolean getWhetherPictureIsPresentInGcs(String pictureKey) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_IS_PICTURE_PRESENT_IN_GCS);
        params.put(BackDoorOperation.PARAMETER_PICTURE_KEY, pictureKey);
        return Boolean.parseBoolean(makePostRequest(params));
    }

    /**
     * Uploads and updates a student's profile picture in the datastore.
     */
    public static String uploadAndUpdateStudentProfilePicture(String googleId, String pictureDataJsonString) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_STUDENT_PROFILE_PICTURE);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        params.put(BackDoorOperation.PARAMETER_PICTURE_DATA, pictureDataJsonString);
        return makePostRequest(params);
    }

    /**
     * Deletes an account from datastore.
     */
    public static String deleteAccount(String googleId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_ACCOUNT);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        return makePostRequest(params);
    }

    /**
     * Persists an instructor data into the datastore.
     */
    public static String createInstructor(InstructorAttributes instructor) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.instructors.put(instructor.googleId, instructor);
        return restoreDataBundle(dataBundle);
    }

    /**
     * Gets an instructor data with particular google ID from the datastore.
     */
    public static InstructorAttributes getInstructorByGoogleId(String googleId, String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID);
        params.put(BackDoorOperation.PARAMETER_GOOGLE_ID, googleId);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String instructorJsonString = makePostRequest(params);
        return JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
    }

    /**
     * Gets an instructor data with particular email from the datastore.
     */
    public static InstructorAttributes getInstructorByEmail(String instructorEmail, String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL, instructorEmail);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String instructorJsonString = makePostRequest(params);
        return JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
    }

    /**
     * Gets the encrypted registration key for an instructor in the datastore.
     */
    public static String getEncryptedKeyForInstructor(String courseId, String instructorEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_ENCRYPTED_KEY_FOR_INSTRUCTOR);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL, instructorEmail);
        return makePostRequest(params);
    }

    /**
     * Deletes an instructor from the datastore.
     */
    public static String deleteInstructor(String courseId, String instructorEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_INSTRUCTOR);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL, instructorEmail);
        return makePostRequest(params);
    }

    /**
     * Persists a course into the datastore.
     */
    public static String createCourse(CourseAttributes course) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.courses.put("dummy-key", course);
        return restoreDataBundle(dataBundle);
    }

    /**
     * Gets a course data from the datastore.
     */
    public static CourseAttributes getCourse(String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_COURSE_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String courseJsonString = makePostRequest(params);
        return JsonUtils.fromJson(courseJsonString, CourseAttributes.class);
    }

    /**
     * Deletes a course from the datastore.
     */
    public static String deleteCourse(String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_COURSE);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        return makePostRequest(params);
    }

    /**
     * Persists a student data into the datastore.
     */
    public static String createStudent(StudentAttributes student) {
        DataBundle dataBundle = new DataBundle();
        dataBundle.students.put("dummy-key", student);
        return restoreDataBundle(dataBundle);
    }

    /**
     * Gets a student data from the datastore.
     */
    public static StudentAttributes getStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_STUDENT_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, studentEmail);
        String studentJson = makePostRequest(params);
        return JsonUtils.fromJson(studentJson, StudentAttributes.class);
    }

    /**
     * Gets list of students data from the datastore.
     */
    public static List<StudentAttributes> getStudents(String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_STUDENTS_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String studentsJson = makePostRequest(params);
        return JsonUtils.fromJson(studentsJson, new TypeToken<List<StudentAttributes>>(){}.getType());
    }

    /**
     * Gets the encrypted registration key for a student in the datastore.
     */
    public static String getEncryptedKeyForStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_ENCRYPTED_KEY_FOR_STUDENT);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, studentEmail);
        return makePostRequest(params);
    }

    /**
     * Edits a student in the datastore.
     */
    public static String editStudent(String originalEmail, StudentAttributes student) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_STUDENT);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, originalEmail);
        params.put(BackDoorOperation.PARAMETER_JSON_STRING, JsonUtils.toJson(student));
        return makePostRequest(params);
    }

    /**
     * Deletes a student from the datastore.
     */
    public static String deleteStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_STUDENT);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_STUDENT_EMAIL, studentEmail);
        return makePostRequest(params);
    }

    /**
     * Gets a feedback session data from the datastore.
     */
    public static FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_SESSION_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        String feedbackSessionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackSessionJson, FeedbackSessionAttributes.class);
    }

    /**
     * Edits a feedback session in the datastore.
     */
    public static String editFeedbackSession(FeedbackSessionAttributes updatedFeedbackSession) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_FEEDBACK_SESSION);
        params.put(BackDoorOperation.PARAMETER_JSON_STRING, JsonUtils.toJson(updatedFeedbackSession));
        return makePostRequest(params);
    }

    /**
     * Deletes a feedback session from the datastore.
     */
    public static String deleteFeedbackSession(String feedbackSessionName, String courseId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_FEEDBACK_SESSION);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        return makePostRequest(params);
    }

    /**
     * Gets a feedback question data from the datastore.
     */
    public static FeedbackQuestionAttributes getFeedbackQuestion(String courseId, String feedbackSessionName,
                                                                 int qnNumber) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_QUESTION_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_COURSE_ID, courseId);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_NUMBER, String.valueOf(qnNumber));
        String feedbackQuestionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
    }

    /**
     * Gets a feedback question data from the datastore.
     */
    public static FeedbackQuestionAttributes getFeedbackQuestion(String questionId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, questionId);
        String feedbackQuestionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
    }

    /**
     * Edits a feedback question in the datastore.
     */
    public static String editFeedbackQuestion(FeedbackQuestionAttributes updatedFeedbackQuestion) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_EDIT_FEEDBACK_QUESTION);
        params.put(BackDoorOperation.PARAMETER_JSON_STRING, JsonUtils.toJson(updatedFeedbackQuestion));
        return makePostRequest(params);
    }

    /**
     * Deletes a feedback question from the datastore.
     */
    public static String deleteFeedbackQuestion(String questionId) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_FEEDBACK_QUESTION);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, questionId);
        return makePostRequest(params);
    }

    /**
     * Persists a feedback response into the datastore.
     */
    public static String createFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        String feedbackResponseJson = JsonUtils.toJson(feedbackResponse);
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_CREATE_FEEDBACK_RESPONSE);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_RESPONSE_JSON, feedbackResponseJson);
        return makePostRequest(params);
    }

    /**
     * Gets a feedback response data from the datastore.
     */
    public static FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId, String giverEmail,
                                                                 String recipient) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, feedbackQuestionId);
        params.put(BackDoorOperation.PARAMETER_GIVER_EMAIL, giverEmail);
        params.put(BackDoorOperation.PARAMETER_RECIPIENT, recipient);
        String feedbackResponseJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackResponseJson, FeedbackResponseAttributes.class);
    }

    /**
     * Gets a list of feedback response data for particular recipient from the datastore.
     */
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

    /**
     * Gets a list of feedback response data for particular giver from the datastore.
     */
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

    /**
     * Deletes a feedback response from the datastore.
     */
    public static String deleteFeedbackResponse(String feedbackQuestionId, String giverEmail, String recipient) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_FEEDBACK_RESPONSE);
        params.put(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID, feedbackQuestionId);
        params.put(BackDoorOperation.PARAMETER_GIVER_EMAIL, giverEmail);
        params.put(BackDoorOperation.PARAMETER_RECIPIENT, recipient);
        return makePostRequest(params);
    }

    private static Map<String, String> createParamMap(BackDoorOperation operation) {
        Map<String, String> map = new HashMap<>();
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
        map.forEach((key, value) -> dataStringBuilder.append(key + "=" + SanitizationHelper.sanitizeForUri(value) + "&"));
        return dataStringBuilder.toString();
    }

    /**
     * Replaces {@link DataBundle#adminEmails} from {@code dataBundle} with an empty map.
     * Using {@link BackDoor} to remove and persist admin emails
     * may affect normal functioning of Admin Emails and remove non-testing data.
     */
    private static void removeAdminEmailsFromDataBundle(DataBundle dataBundle) {
        dataBundle.adminEmails = new HashMap<>();
    }

    /**
     * Checks if a group recipient's file is present in GCS with specified Key.
     */
    public static boolean isGroupListFileKeyPresentInGcs(String groupListFileKey) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_IS_GROUP_LIST_FILE_PRESENT_IN_GCS);
        params.put(BackDoorOperation.PARAMETER_GROUP_LIST_FILE_KEY, groupListFileKey);
        return Boolean.parseBoolean(makePostRequest(params));
    }

    /**
     * Deletes the uploaded test file for testing email using group mode.
     */
    public static String deleteGroupListFile(String groupListFileKey) {
        Map<String, String> params = createParamMap(BackDoorOperation.OPERATION_DELETE_GROUP_LIST_FILE);
        params.put(BackDoorOperation.PARAMETER_GROUP_LIST_FILE_KEY, groupListFileKey);
        return makePostRequest(params);
    }

}
