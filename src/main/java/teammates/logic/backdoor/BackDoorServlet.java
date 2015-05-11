package teammates.logic.backdoor;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Utils;

@SuppressWarnings("serial")
public class BackDoorServlet extends HttpServlet {
    
    /*
     * This class is tested by the BackDoorTest class.
     */

    public static final String OPERATION_CREATE_INSTRUCTOR = "OPERATION_CREATE_INSTRUCTOR";
    public static final String OPERATION_DELETE_INSTRUCTOR = "OPERATION_DELETE_INSTRUCTOR";
    public static final String OPERATION_DELETE_INSTRUCTOR_NON_CASCADE = "OPERATION_DELETE_INSTRUCTOR_NON_CASCADE";
    public static final String OPERATION_DELETE_COURSE = "OPERATION_DELETE_COURSE";
    public static final String OPERATION_DELETE_ACCOUNT = "OPERATION_DELETE_ACCOUNT";
    public static final String OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE = "OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE";
    public static final String OPERATION_DELETE_STUDENT = "OPERATION_DELETE_STUDENT";
    public static final String OPERATION_DELETE_TEAM_FORMING_LOG = "OPERATION_DELETE_TEAM_FORMING_LOG";
    public static final String OPERATION_DELETE_TEAM_PROFILE = "OPERATION_DELETE_TEAM_PROFILE";
    public static final String OPERATION_DELETE_TFS = "OPERATION_DELETE_TFS";
    public static final String OPERATION_DELETE_FEEDBACK_SESSION = "OPERATION_DELETE_FEEDBACK_SESSION";
    public static final String OPERATION_EDIT_ACCOUNT = "OPERATION_EDIT_ACCOUNT";
    public static final String OPERATION_EDIT_FEEDBACK_SESSION = "OPERATION_EDIT_FEEDBACK_SESSION";
    public static final String OPERATION_EDIT_STUDENT = "OPERATION_EDIT_STUDENT";
    public static final String OPERATION_EDIT_STUDENT_PROFILE_PICTURE = "OPERATION_EDIT_STUDENT_PROFILE_PICTURE";
    public static final String OPERATION_EDIT_TEAM_PROFILE = "OPERATION_EDIT_TEAM_PROFILE";
    public static final String OPERATION_EDIT_TFS = "OPERATION_EDIT_TFS";
    public static final String OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID = "OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID";
    public static final String OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL = "OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL";
    public static final String OPERATION_GET_COURSES_BY_INSTRUCTOR = "get_courses_by_instructor";
    public static final String OPERATION_GET_ACCOUNT_AS_JSON = "OPERATION_GET_ACCOUNT_AS_JSON";
    public static final String OPERATION_GET_STUDENTPROFILE_AS_JSON = "OPERATION_GET_STUDENTPROFILE_AS_JSON";
    public static final String OPERATION_GET_COURSE_AS_JSON = "OPERATION_GET_COURSE_AS_JSON";
    public static final String OPERATION_GET_STUDENT_AS_JSON = "OPERATION_GET_STUDENT_AS_JSON";
    public static final String OPERATION_GET_KEY_FOR_INSTRUCTOR = "OPERATION_GET_KEY_FOR_INSTRUCTOR";
    public static final String OPERATION_GET_KEY_FOR_STUDENT = "OPERATION_GET_KEY_FOR_STUDENT";
    public static final String OPERATION_GET_ALL_STUDENTS_AS_JSON = "OPERATION_GET_ALL_STUDENTS";
    public static final String OPERATION_GET_TEAM_FORMING_LOG_AS_JSON = "OPERATION_GET_TEAM_FORMING_LOG_AS_JSON";
    public static final String OPERATION_GET_TEAM_PROFILE_AS_JSON = "OPERATION_GET_TEAM_PROFILE_AS_JSON";
    public static final String OPERATION_GET_TFS_AS_JSON = "OPERATION_GET_TFS_AS_JSON";
    public static final String OPERATION_GET_FEEDBACK_SESSION_AS_JSON = "OPERATION_GET_FEEDBACK_SESSION_AS_JSON";
    public static final String OPERATION_GET_FEEDBACK_RESPONSES_FOR_RECEIVER_AS_JSON = "OPERATION_GET_FEEDBACK_RESPONSES_FOR_RECEIVER_AS_JSON";
    public static final String OPERATION_GET_FEEDBACK_RESPONSES_FOR_GIVER_AS_JSON = "OPERATION_GET_FEEDBACK_RESPONSES_FOR_GIVER_AS_JSON";
    public static final String OPERATION_GET_FEEDBACK_QUESTION_AS_JSON = "OPERATION_GET_FEEDBACK_QUESTION_AS_JSON";
    public static final String OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON = "OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON";
    public static final String OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON = "OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON";
    public static final String OPERATION_IS_PICTURE_PRESENT_IN_GCS = "OPERATION_IS_PICTURE_PRESENT_IN_GCS";
    
    public static final String OPERATION_PUT_DOCUMENTS_FOR_STUDENTS = "OPERATION_PUT_DOCUMENTS_FOR_STUDENTS";

    public static final String OPERATION_PERSIST_DATABUNDLE = "OPERATION_PERSIST_DATABUNDLE";
    public static final String OPERATION_REMOVE_DATABUNDLE = "OPERATION_REMOVE_DATABUNDLE";
    public static final String OPERATION_REMOVE_AND_RESTORE_DATABUNDLE = "OPERATION_REMOVE_AND_RESTORE_DATABUNDLE";
    public static final String OPERATION_SYSTEM_ACTIVATE_AUTOMATED_REMINDER = "activate_auto_reminder";
    public static final String OPERATION_PUT_DOCUMENTS = "OPERATION_PUT_DOCUMENTS";
    
    public static final String PARAMETER_BACKDOOR_KEY = "Params.BACKDOOR_KEY";
    public static final String PARAMETER_BACKDOOR_OPERATION = "PARAMETER_BACKDOOR_OPERATION";
    public static final String PARAMETER_GOOGLE_ID = "PARAMETER_GOOGLE_ID";
    public static final String PARAMETER_COURSE_ID = "PARAMETER_COURSE_ID";
    public static final String PARAMETER_FEEDBACK_QUESTION_ID = "PARAMETER_QUESTION_ID";
    public static final String PARAMETER_INSTRUCTOR_EMAIL = "PARAMETER_INSTRUCTOR_EMAIL";
    public static final String PARAMETER_INSTRUCTOR_ID = "PARAMETER_INSTRUCTOR_ID";
    public static final String PARAMETER_INSTRUCTOR_NAME = "PARAMETER_INSTRUCTOR_NAME";
    public static final String PARAMETER_DATABUNDLE_JSON = "PARAMETER_DATABUNDLE_JSON";
    public static final String PARAMETER_JSON_STRING = "PARAMETER_JASON_STRING";
    public static final String PARAMETER_STUDENT_EMAIL = "PARAMETER_STUDENT_EMAIL";
    public static final String PARAMETER_STUDENT_ID = "PARAMETER_STUDENT_ID";
    public static final String PARAMETER_TEAM_NAME = "PARAMETER_TEAM_NAME";
    public static final String PARAMETER_FEEDBACK_SESSION_NAME = "PARAMETER_FEEDBACK_SESSION_NAME";
    public static final String PARAMETER_FEEDBACK_QUESTION_NUMBER = "PARAMETER_FEEDBACK_QUESTION_NUMBER";
    public static final String PARAMETER_GIVER_EMAIL = "PARAMETER_GIVER_EMAIL";
    public static final String PARAMETER_RECIPIENT = "PARAMETER_RECIPIENT";
    public static final String PARAMETER_PICTURE_KEY = "PARAMETER_PICTURE_KEY";
    public static final String PARAMETER_PICTURE_DATA = "PARAMETER_PICTURE_DATA";
    
    public static final String RETURN_VALUE_TRUE = "true";
    public static final String RETURN_VALUE_FALSE = "false";
    
    
    private static final Logger log = Utils.getLogger();

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doPost(req, resp);
    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String action = req.getParameter(PARAMETER_BACKDOOR_OPERATION);
        log.info(action);

        String returnValue;

        String keyReceived = req.getParameter(PARAMETER_BACKDOOR_KEY);
        if (!keyReceived.equals(Config.BACKDOOR_KEY)) {
            returnValue = "Not authorized to access Backdoor Services";
        } else {
            try {
                returnValue = executeBackendAction(req, action);
            } catch (Exception e) {
                log.info(e.getMessage());
                returnValue = Const.StatusCodes.BACKDOOR_STATUS_FAILURE
                        + TeammatesException.toStringWithStackTrace(e);
            } catch (AssertionError ae) {
                log.info(ae.getMessage());
                returnValue = Const.StatusCodes.BACKDOOR_STATUS_FAILURE
                        + " Assertion error " + ae.getMessage();
            }
        }

        resp.setContentType("text/plain; charset=utf-8");
        resp.getWriter().write(returnValue);
        resp.flushBuffer();
    }

    private String executeBackendAction(HttpServletRequest req, String action)
            throws Exception {
        // TODO: reorder in alphabetical order
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        if (action.equals(OPERATION_DELETE_INSTRUCTOR)) {
            String instructorEmail = req.getParameter(PARAMETER_INSTRUCTOR_EMAIL);
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            backDoorLogic.deleteInstructor(courseId, instructorEmail);
        } else if (action.equals(OPERATION_DELETE_ACCOUNT)) {
            String googleId = req.getParameter(PARAMETER_GOOGLE_ID);
            backDoorLogic.deleteAccount(googleId);
        } else if (action.equals(OPERATION_DELETE_COURSE)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            backDoorLogic.deleteCourse(courseId);
        } else if (action.equals(OPERATION_DELETE_STUDENT)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
            backDoorLogic.deleteStudent(courseId, email);
        } else if (action.equals(OPERATION_GET_ACCOUNT_AS_JSON)) {
            String googleId = req.getParameter(PARAMETER_GOOGLE_ID);
            return backDoorLogic.getAccountAsJson(googleId);
        } else if (action.equals(OPERATION_GET_STUDENTPROFILE_AS_JSON)) {
            String googleId = req.getParameter(PARAMETER_GOOGLE_ID);
            return backDoorLogic.getStudentProfileAsJson(googleId);
        } else if (action.equals(OPERATION_IS_PICTURE_PRESENT_IN_GCS)) {
            String pictureKey = req.getParameter(PARAMETER_PICTURE_KEY);
            return backDoorLogic.isPicturePresentInGcs(pictureKey);
        } else if (action.equals(OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID)) {
            String instructorID = req.getParameter(PARAMETER_INSTRUCTOR_ID);
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            return backDoorLogic.getInstructorAsJsonById(instructorID, courseId);
        } else if (action.equals(OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL)) {
            String instructorEmail = req.getParameter(PARAMETER_INSTRUCTOR_EMAIL);
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            return backDoorLogic.getInstructorAsJsonByEmail(instructorEmail, courseId);
        } else if (action.equals(OPERATION_GET_COURSE_AS_JSON)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            return backDoorLogic.getCourseAsJson(courseId);
        } else if (action.equals(OPERATION_GET_COURSES_BY_INSTRUCTOR)) {
            String instructorID = req.getParameter(PARAMETER_INSTRUCTOR_ID);
            return getCourseIDsForInstructor(instructorID);
        } else if (action.equals(OPERATION_GET_STUDENT_AS_JSON)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
            return backDoorLogic.getStudentAsJson(courseId, email);
        } else if (action.equals(OPERATION_GET_ALL_STUDENTS_AS_JSON)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            return backDoorLogic.getAllStudentsAsJson(courseId);
        } else if (action.equals(OPERATION_GET_KEY_FOR_INSTRUCTOR)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            String email = req.getParameter(PARAMETER_INSTRUCTOR_EMAIL);
            return backDoorLogic.getKeyForInstructor(courseId, email);
        } else if (action.equals(OPERATION_GET_KEY_FOR_STUDENT)) {
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
            return backDoorLogic.getKeyForStudent(courseId, email);
        } else if (action.equals(OPERATION_PERSIST_DATABUNDLE)) {
            String dataBundleJsonString = req
                    .getParameter(PARAMETER_DATABUNDLE_JSON);
            DataBundle dataBundle = Utils.getTeammatesGson().fromJson(
                    dataBundleJsonString, DataBundle.class);
            backDoorLogic.persistDataBundle(dataBundle);
        } else if (action.equals(OPERATION_REMOVE_DATABUNDLE)) {
            String dataBundleJsonString = req
                    .getParameter(PARAMETER_DATABUNDLE_JSON);
            DataBundle dataBundle = Utils.getTeammatesGson().fromJson(
                    dataBundleJsonString, DataBundle.class);
            backDoorLogic.removeDataBundle(dataBundle);
        } else if (action.equals(OPERATION_PUT_DOCUMENTS_FOR_STUDENTS)) {
            String dataBundleJsonString = req
                    .getParameter(PARAMETER_DATABUNDLE_JSON);
            DataBundle dataBundle = Utils.getTeammatesGson().fromJson(
                    dataBundleJsonString, DataBundle.class);
            backDoorLogic.putDocumentsForStudents(dataBundle);
        } else if (action.equals(OPERATION_REMOVE_AND_RESTORE_DATABUNDLE)) {
            String dataBundleJsonString = req
                    .getParameter(PARAMETER_DATABUNDLE_JSON);
            DataBundle dataBundle = Utils.getTeammatesGson().fromJson(
                    dataBundleJsonString, DataBundle.class);
            backDoorLogic.deleteExistingData(dataBundle);
            backDoorLogic.persistDataBundle(dataBundle);
        } else if (action.equals(OPERATION_PUT_DOCUMENTS)) {
            String dataBundleJsonString = req
                    .getParameter(PARAMETER_DATABUNDLE_JSON);
            DataBundle dataBundle = Utils.getTeammatesGson().fromJson(
                    dataBundleJsonString, DataBundle.class);
            backDoorLogic.putDocuments(dataBundle);
        } else if (action.equals(OPERATION_EDIT_ACCOUNT)) {
            String newValues = req.getParameter(PARAMETER_JSON_STRING);
            backDoorLogic.editAccountAsJson(newValues);
        } else if (action.equals(OPERATION_EDIT_FEEDBACK_SESSION)) {
            String newValues = req.getParameter(PARAMETER_JSON_STRING);
            backDoorLogic.editFeedbackSessionAsJson(newValues);
        } else if (action.equals(OPERATION_EDIT_STUDENT)) {
            String originalEmail = req.getParameter(PARAMETER_STUDENT_EMAIL);
            String newValues = req.getParameter(PARAMETER_JSON_STRING);
            backDoorLogic.editStudentAsJson(originalEmail, newValues);
        } else if (action.equals(OPERATION_EDIT_STUDENT_PROFILE_PICTURE)) {
            String picture = req.getParameter(PARAMETER_PICTURE_DATA);
            byte[] pictureData = Utils.getTeammatesGson().fromJson(picture, byte[].class);
            String googleId = req.getParameter(PARAMETER_GOOGLE_ID);
            backDoorLogic.uploadAndUpdateStudentProfilePicture(googleId, pictureData);
        } else if (action.equals(OPERATION_DELETE_FEEDBACK_SESSION)) {
            String feedbackSessionName = req.getParameter(PARAMETER_FEEDBACK_SESSION_NAME);
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            backDoorLogic.deleteFeedbackSession(feedbackSessionName, courseId);
        } else if (action.equals(OPERATION_GET_FEEDBACK_SESSION_AS_JSON)) { 
            String feedbackSessionName = req.getParameter(PARAMETER_FEEDBACK_SESSION_NAME);
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            return backDoorLogic.getFeedbackSessionAsJson(feedbackSessionName, courseId);
        }  else if (action.equals(OPERATION_GET_FEEDBACK_RESPONSES_FOR_GIVER_AS_JSON)) { 
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            String giverEmail = req.getParameter(PARAMETER_GIVER_EMAIL);
            return backDoorLogic.getFeedbackResponsesForGiverAsJson(courseId, giverEmail);
        }  else if (action.equals(OPERATION_GET_FEEDBACK_RESPONSES_FOR_RECEIVER_AS_JSON)) { 
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            String recipient = req.getParameter(PARAMETER_RECIPIENT);
            return backDoorLogic.getFeedbackResponsesForReceiverAsJson(courseId, recipient);
        }  else if (action.equals(OPERATION_GET_FEEDBACK_QUESTION_AS_JSON)) { 
            String feedbackSessionName = req.getParameter(PARAMETER_FEEDBACK_SESSION_NAME);
            String courseId = req.getParameter(PARAMETER_COURSE_ID);
            int qnNumber = Integer.parseInt(req.getParameter(PARAMETER_FEEDBACK_QUESTION_NUMBER));
            return backDoorLogic.getFeedbackQuestionAsJson(feedbackSessionName, courseId, qnNumber);            
        }  else if (action.equals(OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON)) { 
            String questionId = req.getParameter(PARAMETER_FEEDBACK_QUESTION_ID);
            return backDoorLogic.getFeedbackQuestionForIdAsJson(questionId);            
        }  else if (action.equals(OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON)) { 
            String feedbackQuestionId = req.getParameter(PARAMETER_FEEDBACK_QUESTION_ID);
            String giverEmail = req.getParameter(PARAMETER_GIVER_EMAIL);
            String recipient = req.getParameter(PARAMETER_RECIPIENT);
            return backDoorLogic.getFeedbackResponseAsJson(feedbackQuestionId, giverEmail, recipient);            
        } else {
            throw new Exception("Unknown command: " + action);
        }
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    private String getCourseIDsForInstructor(String instructorID) {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        String courseIDs = "";

        try {
            HashMap<String, CourseDetailsBundle> courseList = backDoorLogic
                    .getCourseSummariesForInstructor(instructorID);
            for (String courseId : courseList.keySet()) {
                courseIDs = courseIDs + courseId + " ";
            }
        } catch (EntityDoesNotExistException e) {
            // Instructor does not exist, no action required.
        }

        return courseIDs.trim();
    }

}