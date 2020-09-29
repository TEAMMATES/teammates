package teammates.test;

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

/**
 * Used to access the datastore without going through the UI.
 *
 * <p>It requires an authentication via "backdoor key" so that
 * the access is limited only to the person who deployed the application.
 */
@Deprecated
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
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Removes given data from the datastore.
     *
     * <p>If given entities have already been deleted, it fails silently.
     */
    public static String removeDataBundle(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Removes and restores given data in the datastore. This method is to be called on test startup.
     *
     * <p>Note:  The data associated with the test accounts have to be <strong>manually</strong> removed by removing the data
     * bundle when a test ends because the test accounts are shared across tests.
     *
     * <p>Test data should never be cleared after test in order to prevent incurring additional datastore costs because the
     * test's data may not be accessed in another test. Also although unlikely in normal conditions, when a test fail to
     * remove data bundle on teardown, another test should have no reason to fail.
     *
     * <p>Another reason not to remove associated data after a test is that in case of test failures, it helps to have the
     * associated data in the datastore to debug the failure.
     *
     * <p>This means that removing the data bundle on startup is not always sufficient because a test only knows how
     * to remove its associated data.
     * This is why some tests would fail when they use the same account and use different data.
     * Extending this method to remove data outside its associated data would introduce
     * unnecessary complications such as extra costs and knowing exactly how much data to remove. Removing too much data
     * would not just incur higher datastore costs but we can make tests unexpectedly pass(fail) when the data is expected to
     * be not present(present) in another test.
     *
     * <p>TODO: Hence, we need to explicitly remove the data bundle in tests on teardown to avoid instability of tests.
     * However, removing the data bundle on teardown manually is not a perfect solution because two tests can concurrently
     * access the same account and their data may get mixed up in the process. This is a major problem we need to address.
     */
    public static String removeAndRestoreDataBundle(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", dataBundleJson);
        return makePostRequest(params);
    }

    /**
     * Puts searchable documents for entities into the datastore.
     */
    public static String putDocuments(DataBundle dataBundle) {
        String dataBundleJson = JsonUtils.toJson(dataBundle);
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", dataBundleJson);
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
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", googleId);
        String accountJsonString = makePostRequest(params);
        return JsonUtils.fromJson(accountJsonString, AccountAttributes.class);
    }

    /**
     * Gets a student profile data from the datastore.
     */
    public static StudentProfileAttributes getStudentProfile(String googleId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", googleId);
        String studentProfileJsonString = makePostRequest(params);
        return JsonUtils.fromJson(studentProfileJsonString, StudentProfileAttributes.class);
    }

    /**
     * Checks if a profile picture with the specified key is present in GCS.
     */
    public static boolean getWhetherPictureIsPresentInGcs(String pictureKey) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", pictureKey);
        return Boolean.parseBoolean(makePostRequest(params));
    }

    /**
     * Uploads and updates a student's profile picture in the datastore.
     */
    public static String uploadAndUpdateStudentProfilePicture(String googleId, String pictureDataJsonString) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", googleId);
        params.put("PLACEHOLDER", pictureDataJsonString);
        return makePostRequest(params);
    }

    /**
     * Deletes an account from datastore.
     */
    public static String deleteAccount(String googleId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", googleId);
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
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", googleId);
        params.put("PLACEHOLDER", courseId);
        String instructorJsonString = makePostRequest(params);
        return JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
    }

    /**
     * Gets an instructor data with particular email from the datastore.
     */
    public static InstructorAttributes getInstructorByEmail(String instructorEmail, String courseId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", instructorEmail);
        params.put("PLACEHOLDER", courseId);
        String instructorJsonString = makePostRequest(params);
        return JsonUtils.fromJson(instructorJsonString, InstructorAttributes.class);
    }

    /**
     * Gets the encrypted registration key for an instructor in the datastore.
     */
    public static String getEncryptedKeyForInstructor(String courseId, String instructorEmail) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", instructorEmail);
        return makePostRequest(params);
    }

    /**
     * Deletes an instructor from the datastore.
     */
    public static String deleteInstructor(String courseId, String instructorEmail) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", instructorEmail);
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
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        String courseJsonString = makePostRequest(params);
        return JsonUtils.fromJson(courseJsonString, CourseAttributes.class);
    }

    /**
     * Edits a course in the datastore.
     */
    public static String editCourse(CourseAttributes course) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", JsonUtils.toJson(course));
        return makePostRequest(params);
    }

    /**
     * Deletes a course from the datastore.
     */
    public static String deleteCourse(String courseId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
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
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", studentEmail);
        String studentJson = makePostRequest(params);
        return JsonUtils.fromJson(studentJson, StudentAttributes.class);
    }

    /**
     * Gets list of students data from the datastore.
     */
    public static List<StudentAttributes> getStudents(String courseId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        String studentsJson = makePostRequest(params);
        return JsonUtils.fromJson(studentsJson, new TypeToken<List<StudentAttributes>>(){}.getType());
    }

    /**
     * Gets the encrypted registration key for a student in the datastore.
     */
    public static String getEncryptedKeyForStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", studentEmail);
        return makePostRequest(params);
    }

    /**
     * Edits a student in the datastore.
     */
    public static String editStudent(String originalEmail, StudentAttributes student) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", originalEmail);
        params.put("PLACEHOLDER", JsonUtils.toJson(student));
        return makePostRequest(params);
    }

    /**
     * Deletes a student from the datastore.
     */
    public static String deleteStudent(String courseId, String studentEmail) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", studentEmail);
        return makePostRequest(params);
    }

    /**
     * Gets a feedback session data from the data storage.
     */
    public static FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", feedbackSessionName);
        params.put("PLACEHOLDER", courseId);
        String feedbackSessionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackSessionJson, FeedbackSessionAttributes.class);
    }

    /**
     * Gets a feedback session data from the recycle bin.
     */
    public static FeedbackSessionAttributes getFeedbackSessionFromRecycleBin(String courseId, String feedbackSessionName) {
        Map<String, String> params =
                createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", feedbackSessionName);
        params.put("PLACEHOLDER", courseId);
        String feedbackSessionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackSessionJson, FeedbackSessionAttributes.class);
    }

    /**
     * Edits a feedback session in the datastore.
     */
    public static String editFeedbackSession(FeedbackSessionAttributes updatedFeedbackSession) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", JsonUtils.toJson(updatedFeedbackSession));
        return makePostRequest(params);
    }

    /**
     * Deletes a feedback session from the datastore.
     */
    public static String deleteFeedbackSession(String feedbackSessionName, String courseId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", feedbackSessionName);
        params.put("PLACEHOLDER", courseId);
        return makePostRequest(params);
    }

    /**
     * Gets a feedback question data from the datastore.
     */
    public static FeedbackQuestionAttributes getFeedbackQuestion(String courseId, String feedbackSessionName,
                                                                 int qnNumber) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", feedbackSessionName);
        params.put("PLACEHOLDER", String.valueOf(qnNumber));
        String feedbackQuestionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
    }

    /**
     * Gets a feedback question data from the datastore.
     */
    public static FeedbackQuestionAttributes getFeedbackQuestion(String questionId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", questionId);
        String feedbackQuestionJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
    }

    /**
     * Edits a feedback question in the datastore.
     */
    public static String editFeedbackQuestion(FeedbackQuestionAttributes updatedFeedbackQuestion) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", JsonUtils.toJson(updatedFeedbackQuestion));
        return makePostRequest(params);
    }

    /**
     * Deletes a feedback question from the datastore.
     */
    public static String deleteFeedbackQuestion(String questionId) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", questionId);
        return makePostRequest(params);
    }

    /**
     * Persists a feedback response into the datastore.
     */
    public static String createFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        String feedbackResponseJson = JsonUtils.toJson(feedbackResponse);
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", feedbackResponseJson);
        return makePostRequest(params);
    }

    /**
     * Gets a feedback response data from the datastore.
     */
    public static FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId, String giverEmail,
                                                                 String recipient) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", feedbackQuestionId);
        params.put("PLACEHOLDER", giverEmail);
        params.put("PLACEHOLDER", recipient);
        String feedbackResponseJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackResponseJson, FeedbackResponseAttributes.class);
    }

    /**
     * Gets a list of feedback response data for particular recipient from the datastore.
     */
    public static List<FeedbackResponseAttributes>
            getFeedbackResponsesForReceiverForCourse(String courseId, String recipientEmail) {
        Map<String, String> params =
                createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", recipientEmail);
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
                createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", courseId);
        params.put("PLACEHOLDER", giverEmail);
        String feedbackResponsesJson = makePostRequest(params);
        return JsonUtils.fromJson(feedbackResponsesJson,
                                  new TypeToken<List<FeedbackResponseAttributes>>(){}.getType());
    }

    /**
     * Deletes a feedback response from the datastore.
     */
    public static String deleteFeedbackResponse(String feedbackQuestionId, String giverEmail, String recipient) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", feedbackQuestionId);
        params.put("PLACEHOLDER", giverEmail);
        params.put("PLACEHOLDER", recipient);
        return makePostRequest(params);
    }

    private static Map<String, String> createParamMap(String operation) {
        Map<String, String> map = new HashMap<>();
        map.put("PLACEHOLDER", operation);

        // For authentication
        map.put("PLACEHOLDER", "BACKDOOR_KEY");

        return map;
    }

    private static String makePostRequest(Map<String, String> map) {
        try {
            String paramString = encodeParameters(map);
            String urlString = "/backdoor";
            URLConnection conn = getConnectionToUrl(urlString);
            sendRequest(paramString, conn);
            return readResponse(conn);
        } catch (Exception e) {
            return TeammatesException.toStringWithStackTrace(e);
        }
    }

    @SuppressWarnings("PMD.AssignmentInOperand") // necessary for reading stream response
    private static String readResponse(URLConnection conn) throws IOException {
        conn.setReadTimeout(10000);
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), Const.SystemParams.ENCODING);
                BufferedReader rd = new BufferedReader(isr)) {
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static void sendRequest(String paramString, URLConnection conn) throws IOException {
        try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), Const.SystemParams.ENCODING)) {
            wr.write(paramString);
            wr.flush();
        }
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
     * Checks if a group recipient's file is present in GCS with specified Key.
     */
    public static boolean isGroupListFileKeyPresentInGcs(String groupListFileKey) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", groupListFileKey);
        return Boolean.parseBoolean(makePostRequest(params));
    }

    /**
     * Deletes the uploaded test file for testing email using group mode.
     */
    public static String deleteGroupListFile(String groupListFileKey) {
        Map<String, String> params = createParamMap("PLACEHOLDER");
        params.put("PLACEHOLDER", groupListFileKey);
        return makePostRequest(params);
    }

}
