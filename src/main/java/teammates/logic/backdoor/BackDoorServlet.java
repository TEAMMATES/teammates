package teammates.logic.backdoor;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

/**
 * Servlet for the BackDoor API.
 *
 * <p>It first checks for authentication (backdoor key) and then forwards the
 * API call to the correct method as specified by the supplied parameters.
 *
 * <p>Each authorized API call will return either the return value of the called method,
 * or a status code indicating the status of the API call.
 *
 * @see BackDoorLogic
 * @see BackDoorOperation
 */
@SuppressWarnings("serial")
public class BackDoorServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=utf-8");

        String keyReceived = req.getParameter(BackDoorOperation.PARAMETER_BACKDOOR_KEY);
        boolean isAuthorized = keyReceived.equals(Config.BACKDOOR_KEY);
        if (isAuthorized) {
            String action = req.getParameter(BackDoorOperation.PARAMETER_BACKDOOR_OPERATION);
            log.info(action);

            BackDoorOperation opCode = BackDoorOperation.valueOf(action);
            String returnValue;
            try {
                returnValue = executeBackEndAction(req, opCode);
            } catch (Exception | AssertionError e) {
                log.info(e.getMessage());
                returnValue = Const.StatusCodes.BACKDOOR_STATUS_FAILURE + " "
                              + TeammatesException.toStringWithStackTrace(e);
            }
            resp.getWriter().write(returnValue);
        } else {
            resp.getWriter().write("Not authorized to access Backdoor Services");
        }
        resp.flushBuffer();
    }

    @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault") // no default so that each case is accounted for
    private String executeBackEndAction(HttpServletRequest req, BackDoorOperation opCode)
            throws IOException, InvalidParametersException, EntityDoesNotExistException {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        switch (opCode) {
        case OPERATION_DELETE_ACCOUNT:
            String googleId = req.getParameter(BackDoorOperation.PARAMETER_GOOGLE_ID);
            backDoorLogic.deleteAccount(googleId);
            break;
        case OPERATION_DELETE_COURSE:
            String courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            backDoorLogic.deleteCourse(courseId);
            break;
        case OPERATION_DELETE_FEEDBACK_QUESTION:
            String questionId = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID);
            backDoorLogic.deleteFeedbackQuestion(questionId);
            break;
        case OPERATION_DELETE_FEEDBACK_RESPONSE:
            String feedbackQuestionId = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID);
            String giverEmail = req.getParameter(BackDoorOperation.PARAMETER_GIVER_EMAIL);
            String recipient = req.getParameter(BackDoorOperation.PARAMETER_RECIPIENT);
            FeedbackResponseAttributes fr =
                    backDoorLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
            backDoorLogic.deleteFeedbackResponse(fr);
            break;
        case OPERATION_DELETE_FEEDBACK_SESSION:
            String feedbackSessionName = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME);
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            backDoorLogic.deleteFeedbackSession(feedbackSessionName, courseId);
            break;
        case OPERATION_DELETE_INSTRUCTOR:
            String instructorEmail = req.getParameter(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL);
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            backDoorLogic.deleteInstructor(courseId, instructorEmail);
            break;
        case OPERATION_DELETE_STUDENT:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            String studentEmail = req.getParameter(BackDoorOperation.PARAMETER_STUDENT_EMAIL);
            backDoorLogic.deleteStudent(courseId, studentEmail);
            break;
        case OPERATION_EDIT_COURSE:
            String newValues = req.getParameter(BackDoorOperation.PARAMETER_JSON_STRING);
            backDoorLogic.editCourseAsJson(newValues);
            break;
        case OPERATION_EDIT_FEEDBACK_QUESTION:
            newValues = req.getParameter(BackDoorOperation.PARAMETER_JSON_STRING);
            backDoorLogic.editFeedbackQuestionAsJson(newValues);
            break;
        case OPERATION_EDIT_FEEDBACK_SESSION:
            newValues = req.getParameter(BackDoorOperation.PARAMETER_JSON_STRING);
            backDoorLogic.editFeedbackSessionAsJson(newValues);
            break;
        case OPERATION_EDIT_STUDENT:
            studentEmail = req.getParameter(BackDoorOperation.PARAMETER_STUDENT_EMAIL);
            newValues = req.getParameter(BackDoorOperation.PARAMETER_JSON_STRING);
            backDoorLogic.editStudentAsJson(studentEmail, newValues);
            break;
        case OPERATION_EDIT_STUDENT_PROFILE_PICTURE:
            String pictureDataJsonString = req.getParameter(BackDoorOperation.PARAMETER_PICTURE_DATA);
            byte[] pictureData = JsonUtils.fromJson(pictureDataJsonString, byte[].class);
            googleId = req.getParameter(BackDoorOperation.PARAMETER_GOOGLE_ID);
            backDoorLogic.uploadAndUpdateStudentProfilePicture(googleId, pictureData);
            break;
        case OPERATION_GET_ACCOUNT_AS_JSON:
            googleId = req.getParameter(BackDoorOperation.PARAMETER_GOOGLE_ID);
            return backDoorLogic.getAccountAsJson(googleId);
        case OPERATION_GET_COURSE_AS_JSON:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            return backDoorLogic.getCourseAsJson(courseId);
        case OPERATION_GET_ENCRYPTED_KEY_FOR_INSTRUCTOR:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            studentEmail = req.getParameter(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL);
            return backDoorLogic.getEncryptedKeyForInstructor(courseId, studentEmail);
        case OPERATION_GET_ENCRYPTED_KEY_FOR_STUDENT:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            studentEmail = req.getParameter(BackDoorOperation.PARAMETER_STUDENT_EMAIL);
            return backDoorLogic.getEncryptedKeyForStudent(courseId, studentEmail);
        case OPERATION_GET_FEEDBACK_QUESTION_AS_JSON:
            feedbackSessionName = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME);
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            int qnNumber = Integer.parseInt(req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_NUMBER));
            return backDoorLogic.getFeedbackQuestionAsJson(feedbackSessionName, courseId, qnNumber);
        case OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON:
            questionId = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID);
            return backDoorLogic.getFeedbackQuestionForIdAsJson(questionId);
        case OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON:
            feedbackQuestionId = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_QUESTION_ID);
            giverEmail = req.getParameter(BackDoorOperation.PARAMETER_GIVER_EMAIL);
            recipient = req.getParameter(BackDoorOperation.PARAMETER_RECIPIENT);
            return backDoorLogic.getFeedbackResponseAsJson(feedbackQuestionId, giverEmail, recipient);
        case OPERATION_GET_FEEDBACK_RESPONSES_FOR_GIVER_AS_JSON:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            giverEmail = req.getParameter(BackDoorOperation.PARAMETER_GIVER_EMAIL);
            return backDoorLogic.getFeedbackResponsesForGiverAsJson(courseId, giverEmail);
        case OPERATION_GET_FEEDBACK_RESPONSES_FOR_RECEIVER_AS_JSON:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            recipient = req.getParameter(BackDoorOperation.PARAMETER_RECIPIENT);
            return backDoorLogic.getFeedbackResponsesForReceiverAsJson(courseId, recipient);
        case OPERATION_GET_FEEDBACK_SESSION_AS_JSON:
            feedbackSessionName = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_SESSION_NAME);
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            return backDoorLogic.getFeedbackSessionAsJson(feedbackSessionName, courseId);
        case OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID:
            googleId = req.getParameter(BackDoorOperation.PARAMETER_GOOGLE_ID);
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            return backDoorLogic.getInstructorAsJsonById(googleId, courseId);
        case OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL:
            instructorEmail = req.getParameter(BackDoorOperation.PARAMETER_INSTRUCTOR_EMAIL);
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            return backDoorLogic.getInstructorAsJsonByEmail(instructorEmail, courseId);
        case OPERATION_GET_STUDENT_AS_JSON:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            studentEmail = req.getParameter(BackDoorOperation.PARAMETER_STUDENT_EMAIL);
            return backDoorLogic.getStudentAsJson(courseId, studentEmail);
        case OPERATION_GET_STUDENTS_AS_JSON:
            courseId = req.getParameter(BackDoorOperation.PARAMETER_COURSE_ID);
            return backDoorLogic.getAllStudentsAsJson(courseId);
        case OPERATION_GET_STUDENTPROFILE_AS_JSON:
            googleId = req.getParameter(BackDoorOperation.PARAMETER_GOOGLE_ID);
            return backDoorLogic.getStudentProfileAsJson(googleId);
        case OPERATION_IS_PICTURE_PRESENT_IN_GCS:
            String pictureKey = req.getParameter(BackDoorOperation.PARAMETER_PICTURE_KEY);
            return String.valueOf(backDoorLogic.isPicturePresentInGcs(pictureKey));
        case OPERATION_CREATE_FEEDBACK_RESPONSE:
            String feedbackResponseJsonString = req.getParameter(BackDoorOperation.PARAMETER_FEEDBACK_RESPONSE_JSON);
            FeedbackResponseAttributes feedbackResponse =
                    JsonUtils.fromJson(feedbackResponseJsonString, FeedbackResponseAttributes.class);
            return backDoorLogic.createFeedbackResponseAndUpdateSessionRespondents(feedbackResponse);
        case OPERATION_PERSIST_DATABUNDLE:
            String dataBundleJsonString = req.getParameter(BackDoorOperation.PARAMETER_DATABUNDLE_JSON);
            DataBundle dataBundle = JsonUtils.fromJson(dataBundleJsonString, DataBundle.class);
            backDoorLogic.persistDataBundle(dataBundle);
            break;
        case OPERATION_PUT_DOCUMENTS:
            dataBundleJsonString = req.getParameter(BackDoorOperation.PARAMETER_DATABUNDLE_JSON);
            dataBundle = JsonUtils.fromJson(dataBundleJsonString, DataBundle.class);
            backDoorLogic.putDocuments(dataBundle);
            break;
        case OPERATION_REMOVE_AND_RESTORE_DATABUNDLE:
            dataBundleJsonString = req.getParameter(BackDoorOperation.PARAMETER_DATABUNDLE_JSON);
            dataBundle = JsonUtils.fromJson(dataBundleJsonString, DataBundle.class);
            backDoorLogic.removeDataBundle(dataBundle);
            backDoorLogic.persistDataBundle(dataBundle);
            break;
        case OPERATION_REMOVE_DATABUNDLE:
            dataBundleJsonString = req.getParameter(BackDoorOperation.PARAMETER_DATABUNDLE_JSON);
            dataBundle = JsonUtils.fromJson(dataBundleJsonString, DataBundle.class);
            backDoorLogic.removeDataBundle(dataBundle);
            break;
        case OPERATION_IS_GROUP_LIST_FILE_PRESENT_IN_GCS:
            String groupListKey = req.getParameter(BackDoorOperation.PARAMETER_GROUP_LIST_FILE_KEY);
            return String.valueOf(backDoorLogic.isGroupListFilePresentInGcs(groupListKey));
        case OPERATION_DELETE_GROUP_LIST_FILE:
            String groupListFileKey = req.getParameter(BackDoorOperation.PARAMETER_GROUP_LIST_FILE_KEY);
            backDoorLogic.deleteGroupListFile(groupListFileKey);
            break;

        }
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

}
