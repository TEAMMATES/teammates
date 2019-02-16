package teammates.ui.controller;

// CHECKSTYLE.OFF:AvoidStarImport as there would be many (>100) import lines added if we were to import all of the ActionURIs
import static teammates.common.util.Const.ActionURIs.*;
// CHECKSTYLE.ON:AvoidStarImport

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.PageNotFoundException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Logger;

/**
 * Is used to generate the matching {@link Action} for a given URI.
 */
public class ActionFactory {
    private static final Logger log = Logger.getLogger();

    private static Map<String, Class<? extends Action>> actionMappings = new HashMap<>();

    static {
        map(INSTRUCTOR_FEEDBACK_RESULTS_PAGE, InstructorFeedbackResultsPageAction.class);
        map(INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD, InstructorFeedbackResultsDownloadAction.class);
        map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD, InstructorFeedbackResponseCommentAddAction.class);
        map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT, InstructorFeedbackResponseCommentEditAction.class);
        map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE, InstructorFeedbackResponseCommentDeleteAction.class);
        map(INSTRUCTOR_STUDENT_LIST_PAGE, InstructorStudentListPageAction.class);
        map(INSTRUCTOR_STUDENT_LIST_AJAX_PAGE, InstructorStudentListAjaxPageAction.class);
        map(INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE, InstructorStudentRecordsAjaxPageAction.class);

        map(STUDENT_FEEDBACK_RESULTS_PAGE, StudentFeedbackResultsPageAction.class);
        map(FEEDBACK_PARTICIPANT_FEEDBACK_RESPONSE_COMMENT_DELETE,
                FeedbackParticipantFeedbackResponseCommentDeleteAction.class);
        map(STUDENT_PROFILE_PICTURE_UPLOAD, StudentProfilePictureUploadAction.class);
        map(STUDENT_PROFILE_PICTURE_EDIT, StudentProfilePictureEditAction.class);
        map(STUDENT_PROFILE_CREATEUPLOADFORMURL, StudentProfileCreateFormUrlAction.class);

        map(CREATE_IMAGE_UPLOAD_URL, CreateImageUploadUrlAction.class);
        map(IMAGE_UPLOAD, ImageUploadAction.class);
    }

    /**
     * Returns the matching {@link Action} object for the URI in the {@code req}.
     */
    public Action getAction(HttpServletRequest req) {

        String url = req.getRequestURL().toString();
        log.info("URL received : [" + req.getMethod() + "] " + url);

        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        Action c = getAction(uri);
        c.init(req);
        return c;

    }

    private static Action getAction(String uri) {
        Class<? extends Action> controllerClass = actionMappings.get(uri);

        if (controllerClass == null) {
            throw new PageNotFoundException(uri);
        }

        try {
            return controllerClass.newInstance();
        } catch (Exception e) {
            Assumption.fail("Could not create the action for " + uri + ": "
                            + TeammatesException.toStringWithStackTrace(e));
            return null;

        }

    }

    private static void map(String actionUri, Class<? extends Action> actionClass) {
        actionMappings.put(actionUri, actionClass);
    }

}
