package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.StudentFeedbackResultsPageData;

public class StudentFeedbackResultsPageAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (courseId == null || feedbackSessionName == null) {
            return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
        }

        if (!isJoinedCourse(courseId)) {
            return createPleaseJoinCourseResponse(courseId);
        }

        gateKeeper.verifyAccessible(getCurrentStudent(courseId),
                                    logic.getFeedbackSession(feedbackSessionName, courseId));

        StudentFeedbackResultsPageData data = new StudentFeedbackResultsPageData(account, student, sessionToken);

        data.student = getCurrentStudent(courseId);
        data.setBundle(logic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, data.student.email));

        if (data.getBundle() == null) {
            // not covered because GateKeeper will detect this as unauthorized exception, but we can
            // leave this here as a safety net on the off cases that GateKeeper fails to catch the Exception
            throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName
                                                  + " does not exist in " + courseId + ".");
        }

        if (!data.getBundle().feedbackSession.isPublished()) {
            throw new UnauthorizedAccessException("This feedback session is not yet visible.");
        }

        if (data.getBundle().isStudentHasSomethingNewToSee(data.student)) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESULTS_SOMETHINGNEW,
                                               StatusMessageColor.INFO));
        } else {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESULTS_NOTHINGNEW,
                                               StatusMessageColor.WARNING));
        }

        statusToAdmin = "Show student feedback result page<br>"
                        + "Session Name: " + feedbackSessionName + "<br>"
                        + "Course ID: " + courseId;

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses =
                                        data.getBundle().getQuestionResponseMapSortedByRecipient();
        data.init(questionsWithResponses);
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, data);
    }

    private StudentAttributes getCurrentStudent(String courseId) {
        if (student == null) {
            return logic.getStudentForGoogleId(courseId, account.googleId);
        }
        return student;
    }
}
