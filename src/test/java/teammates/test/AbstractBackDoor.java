package teammates.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.output.AccountData;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.CourseData;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackQuestionsData;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.NotificationData;
import teammates.ui.output.StudentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;

import tools.jackson.databind.JsonNode;

/**
 * Used to create API calls to the back-end without going through the UI.
 */
public abstract class AbstractBackDoor {

    /**
     * Gets the URL of the back-end.
     */
    protected abstract String getAppUrl();

    /**
     * Gets the backdoor key used to authenticate with the back-end.
     */
    protected abstract String getBackdoorKey();

    /**
     * Gets the CSRF key used to authenticate with the back-end.
     */
    protected abstract String getCsrfKey();

    /**
     * Executes GET request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public ResponseBodyAndCode executeGetRequest(String relativeUrl, Map<String, String> params) {
        return executeRequest(HttpGet.METHOD_NAME, relativeUrl, params, null);
    }

    /**
     * Executes POST request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public ResponseBodyAndCode executePostRequest(String relativeUrl, Map<String, String> params, String body) {
        return executeRequest(HttpPost.METHOD_NAME, relativeUrl, params, body);
    }

    /**
     * Executes PUT request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public ResponseBodyAndCode executePutRequest(String relativeUrl, Map<String, String> params, String body) {
        return executeRequest(HttpPut.METHOD_NAME, relativeUrl, params, body);
    }

    /**
     * Executes DELETE request with the given {@code relativeUrl}.
     *
     * @return The body content and status of the HTTP response
     */
    public ResponseBodyAndCode executeDeleteRequest(String relativeUrl, Map<String, String> params) {
        return executeRequest(HttpDelete.METHOD_NAME, relativeUrl, params, null);
    }

    /**
     * Executes HTTP request with the given {@code method} and {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    private ResponseBodyAndCode executeRequest(
            String method, String relativeUrl, Map<String, String> params, String body) {
        String url = getAppUrl() + relativeUrl;

        HttpRequestBase request;
        switch (method) {
        case HttpGet.METHOD_NAME:
            request = createGetRequest(url, params);
            break;
        case HttpPost.METHOD_NAME:
            request = createPostRequest(url, params, body);
            break;
        case HttpPut.METHOD_NAME:
            request = createPutRequest(url, params, body);
            break;
        case HttpDelete.METHOD_NAME:
            request = createDeleteRequest(url, params);
            break;
        default:
            throw new RuntimeException("Unaccepted HTTP method: " + method);
        }

        addAuthKeys(request);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            String responseBody = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(entity.getContent(), Const.ENCODING))) {
                    responseBody = br.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
            return new ResponseBodyAndCode(responseBody, response.getStatusLine().getStatusCode());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes GET request with the given {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    private static HttpGet createGetRequest(String url, Map<String, String> params) {
        return new HttpGet(createBasicUri(url, params));
    }

    private static HttpPost createPostRequest(String url, Map<String, String> params, String body) {
        HttpPost post = new HttpPost(createBasicUri(url, params));

        if (body != null) {
            StringEntity entity = new StringEntity(body, Const.ENCODING);
            post.setEntity(entity);
        }

        return post;
    }

    private static HttpPut createPutRequest(String url, Map<String, String> params, String body) {
        HttpPut put = new HttpPut(createBasicUri(url, params));

        if (body != null) {
            StringEntity entity = new StringEntity(body, Const.ENCODING);
            put.setEntity(entity);
        }

        return put;
    }

    private static HttpDelete createDeleteRequest(String url, Map<String, String> params) {
        return new HttpDelete(createBasicUri(url, params));
    }

    private static URI createBasicUri(String url, Map<String, String> params) {
        List<NameValuePair> postParameters = new ArrayList<>();
        if (params != null) {
            params.forEach((key, value) -> postParameters.add(new BasicNameValuePair(key, value)));
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameters(postParameters);

            return uriBuilder.build();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private void addAuthKeys(HttpRequestBase request) {
        request.addHeader(Const.HeaderNames.BACKDOOR_KEY, getBackdoorKey());
        request.addHeader(Const.HeaderNames.CSRF_KEY, getCsrfKey());
    }

    /**
     * Removes and restores given data in the database. This method is to be called on test startup.
     */
    public DataBundle removeAndRestoreDataBundle(DataBundle dataBundle) throws HttpRequestFailedException {
        removeDataBundle(dataBundle);
        ResponseBodyAndCode putRequestOutput =
                executePostRequest(Const.ResourceURIs.DATABUNDLE, null, JsonUtils.toJson(dataBundle));
        if (putRequestOutput.responseCode != HttpStatus.SC_OK) {
            throw new HttpRequestFailedException("Request failed: [" + putRequestOutput.responseCode + "] "
                    + putRequestOutput.responseBody);
        }

        JsonNode jsonObject = JsonUtils.parse(putRequestOutput.responseBody);
        // data bundle is nested under message key
        String message = jsonObject.get("message").asText();
        return JsonUtils.fromJson(message, DataBundle.class);
    }

    /**
     * Removes given data from the database.
     *
     * <p>If given entities have already been deleted, it fails silently.
     */
    public void removeDataBundle(DataBundle dataBundle) {
        executePutRequest(Const.ResourceURIs.DATABUNDLE, null, JsonUtils.toJson(dataBundle));
    }

    /**
     * Gets the cookie format for the given user ID.
     */
    public String getUserCookie(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.USER_ID, userId);
        ResponseBodyAndCode response = executePostRequest(Const.ResourceURIs.USER_COOKIE, params, null);

        MessageOutput output = JsonUtils.fromJson(response.responseBody, MessageOutput.class);
        return output.getMessage();
    }

    /**
     * Gets account data from the database.
     */
    public AccountData getAccountData(String googleId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.INSTRUCTOR_ID, googleId);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.ACCOUNT, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        return JsonUtils.fromJson(response.responseBody, AccountData.class);
    }

    /**
     * Gets course data from the database.
     */
    public CourseData getCourseData(String courseId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, courseId);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.COURSE, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        return JsonUtils.fromJson(response.responseBody, CourseData.class);
    }

    /**
     * Returns true if the course exists and is in recycle bin.
     */
    public boolean isCourseInRecycleBin(String courseId) {
        CourseData courseData = getCourseData(courseId);
        if (courseData == null) {
            return false;
        }
        return courseData.getDeletionTimestamp() != 0;
    }

    /**
     * Gets instructor data from the database.
     */
    public InstructorData getInstructorData(String courseId, String email) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, courseId);
        params.put(Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString());
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.INSTRUCTORS, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        InstructorsData instructorsData = JsonUtils.fromJson(response.responseBody, InstructorsData.class);
        InstructorData instructorData = instructorsData.getInstructors()
                .stream()
                .filter(instructor -> instructor.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (instructorData == null) {
            return null;
        }

        return instructorData;
    }

    /**
     * Gets student data from the database.
     */
    public StudentData getStudentData(String courseId, String studentEmail) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, courseId);
        params.put(Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.STUDENT, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }
        return JsonUtils.fromJson(response.responseBody, StudentData.class);
    }

    /**
     * Get feedback session data from database.
     */
    public FeedbackSessionData getFeedbackSessionData(UUID feedbackSessionId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId.toString());
        params.put(Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString());
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.SESSION, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }
        return JsonUtils.fromJson(response.responseBody, FeedbackSessionData.class);
    }

    /**
     * Get feedback sessions of a course from database.
     */
    public List<FeedbackSessionData> getFeedbackSessionsForCourse(String courseId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, courseId);
        params.put(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.SESSIONS, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return Collections.emptyList();
        }
        FeedbackSessionsData sessionsData = JsonUtils.fromJson(response.responseBody, FeedbackSessionsData.class);
        return sessionsData.getFeedbackSessions();
    }

    /**
     * Get soft deleted feedback session from database.
     */
    public FeedbackSessionData getSoftDeletedSessionData(String feedbackSessionName, String instructorId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR);
        params.put(Const.ParamsNames.IS_IN_RECYCLE_BIN, "true");
        params.put(Const.ParamsNames.USER_ID, instructorId);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.SESSIONS, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        FeedbackSessionsData sessionsData = JsonUtils.fromJson(response.responseBody, FeedbackSessionsData.class);
        return sessionsData.getFeedbackSessions()
                .stream()
                .filter(fs -> fs.getFeedbackSessionName().equals(feedbackSessionName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get feedback question data from database.
     */
    public FeedbackQuestionData getFeedbackQuestionData(int questionNumber, UUID feedbackSessionId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId.toString());
        params.put(Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString());
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.QUESTIONS, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        FeedbackQuestionsData questionsData = JsonUtils.fromJson(response.responseBody, FeedbackQuestionsData.class);
        return questionsData.getQuestions()
                .stream()
                .filter(fq -> fq.getQuestionNumber() == questionNumber)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get feedback response data from database.
     */
    public FeedbackResponseData getFeedbackResponseData(String feedbackQuestionId, String giver,
                                                                String recipient) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
        params.put(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
        params.put(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, giver);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.RESPONSES, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        FeedbackResponsesData responsesData = JsonUtils.fromJson(response.responseBody, FeedbackResponsesData.class);
        return responsesData.getResponses()
                .stream()
                .filter(r -> r.getGiverIdentifier().equals(giver) && r.getRecipientIdentifier().equals(recipient))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get feedback response comment data from database.
     */
    public FeedbackResponseCommentData getFeedbackResponseCommentData(String feedbackResponseId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        params.put(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.RESPONSE_COMMENT, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND
                || response.responseCode == HttpStatus.SC_NO_CONTENT) {
            return null;
        }

        return JsonUtils.fromJson(response.responseBody, FeedbackResponseCommentData.class);
    }

    /**
     * Updates a feedback response comment via the backdoor.
     * This triggers a new updatedAt timestamp in the database.
     *
     * @param commentId the ID of the comment to update
     * @param commentText the new comment text
     * @param instructorGoogleId the Google ID of an instructor with permission to modify comments
     */
    public void updateFeedbackResponseComment(UUID commentId, String commentText, String instructorGoogleId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, commentId.toString());
        params.put(Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString());
        params.put(Const.ParamsNames.USER_ID, instructorGoogleId);

        FeedbackResponseCommentUpdateRequest body = new FeedbackResponseCommentUpdateRequest(
                commentText,
                new ArrayList<>(),
                new ArrayList<>()
        );

        executePutRequest(Const.ResourceURIs.RESPONSE_COMMENT, params, JsonUtils.toJson(body));
    }

    /**
     * Deletes a course from the database.
     */
    public void deleteCourse(String courseId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, courseId);
        executeDeleteRequest(Const.ResourceURIs.COURSE, params);
    }

    /**
     * Gets an account request from the database.
     */
    public AccountRequestData getAccountRequest(UUID id) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString());

        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.ACCOUNT_REQUEST, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        return JsonUtils.fromJson(response.responseBody, AccountRequestData.class);
    }

    /**
     * Gets registration key of an account request from the database.
     */
    public String getRegKeyForAccountRequest(UUID id) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString());

        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.ACCOUNT_REQUEST, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        return JsonUtils.fromJson(response.responseBody, AccountRequestData.class).getRegistrationKey();
    }

    /**
     * Deletes an account request from the database.
     */
    public void deleteAccountRequest(UUID id) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString());
        executeDeleteRequest(Const.ResourceURIs.ACCOUNT_REQUEST, params);
    }

    /**
     * Gets notification data from the database.
     */
    public NotificationData getNotificationData(String notificationId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.NOTIFICATION_ID, notificationId);
        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.NOTIFICATION, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }
        return JsonUtils.fromJson(response.responseBody, NotificationData.class);
    }

    /**
     * Deletes a notification from the database.
     */
    public void deleteNotification(String notificationId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.NOTIFICATION_ID, notificationId);
        executeDeleteRequest(Const.ResourceURIs.NOTIFICATION, params);
    }

    /**
     * Deletes a notification from the database.
     */
    public void deleteNotification(UUID notificationId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.NOTIFICATION_ID, notificationId.toString());
        executeDeleteRequest(Const.ResourceURIs.NOTIFICATION, params);
    }

    /**
     * Gets feedback session deadline extensions data from the database.
     */
    public DeadlineExtensionsData getDeadlineExtensionsData(
            String feedbackSessionId) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId);

        ResponseBodyAndCode response = executeGetRequest(Const.ResourceURIs.SESSION_DEADLINE_EXTENSIONS, params);
        if (response.responseCode == HttpStatus.SC_NOT_FOUND) {
            return null;
        }

        return JsonUtils.fromJson(response.responseBody, DeadlineExtensionsData.class);
    }

    private static final class ResponseBodyAndCode {

        String responseBody;
        int responseCode;

        ResponseBodyAndCode(String responseBody, int responseCode) {
            this.responseBody = responseBody;
            this.responseCode = responseCode;
        }

    }
}
