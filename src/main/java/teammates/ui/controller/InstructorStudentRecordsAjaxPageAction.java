package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.SessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorStudentRecordsAjaxPageData;

public class InstructorStudentRecordsAjaxPageAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentEmail);

        String targetSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, targetSessionName);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);

        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS,
                                               StatusMessageColor.DANGER));
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }

        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsListForInstructor(account.googleId, false);

        filterFeedbackSessions(courseId, feedbacks, instructor, student);

        List<SessionAttributes> sessions = new ArrayList<>();
        sessions.addAll(feedbacks);
        sessions.sort(SessionAttributes.DESCENDING_ORDER);

        List<FeedbackSessionResultsBundle> results = new ArrayList<>();
        for (SessionAttributes session : sessions) {
            if (session instanceof FeedbackSessionAttributes) {
                if (!targetSessionName.isEmpty() && targetSessionName.equals(session.getSessionName())) {
                    FeedbackSessionResultsBundle result = logic.getFeedbackSessionResultsForInstructor(
                                                    session.getSessionName(), courseId, instructor.email);
                    results.add(result);
                }
            } else {
                Assumption.fail("Unknown session type");
            }
        }
        statusToAdmin = "instructorStudentRecords Ajax Page Load<br>"
                      + "Viewing <span class=\"bold\">" + studentEmail + "'s</span> records "
                      + "for session <span class=\"bold\">[" + targetSessionName + "]</span> "
                      + "in course <span class=\"bold\">[" + courseId + "]</span>";

        InstructorStudentRecordsAjaxPageData data =
                                        new InstructorStudentRecordsAjaxPageData(account, student, sessionToken, results);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX, data);
    }

    private void filterFeedbackSessions(String courseId, List<FeedbackSessionAttributes> feedbacks,
                                        InstructorAttributes currentInstructor, StudentAttributes student) {
        feedbacks.removeIf(tempFs -> !tempFs.getCourseId().equals(courseId)
                    || !currentInstructor.isAllowedForPrivilege(student.section, tempFs.getSessionName(),
                                              Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));

    }

}
