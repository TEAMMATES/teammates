package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentRecordsAjaxPageAction extends Action {

    private InstructorStudentRecordsAjaxPageData data;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);

        String targetSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(targetSessionName);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);

        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));

        data = new InstructorStudentRecordsAjaxPageData(account);

        data.student = logic.getStudentForEmail(courseId, studentEmail);
        if (data.student == null) {
            statusToUser.add(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS);
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }

        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsListForInstructor(account.googleId);

        filterFeedbackSessions(courseId, feedbacks, instructor);

        List<SessionAttributes> sessions = new ArrayList<SessionAttributes>();
        sessions.addAll(feedbacks);
        Collections.sort(sessions, SessionAttributes.DESCENDING_ORDER);

        data.results = new ArrayList<SessionResultsBundle>();
        for (SessionAttributes session : sessions) {
            if (session instanceof FeedbackSessionAttributes) {
                if (!targetSessionName.isEmpty() && targetSessionName.equals(session.getSessionName())) {
                    SessionResultsBundle result = logic.getFeedbackSessionResultsForInstructor(
                                                    session.getSessionName(), courseId, instructor.email);
                    data.results.add(result);
                }
            } else {
                Assumption.fail("Unknown session type");
            }
        }
        statusToAdmin = "instructorStudentRecords Ajax Page Load<br>"
                      + "Viewing <span class=\"bold\">" + studentEmail + "'s</span> records "
                      + "for session <span class=\"bold\">[" + targetSessionName + "]</span> "
                      + "in course <span class=\"bold\">[" + courseId + "]</span>";

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX, data);
    }

    private void filterFeedbackSessions(String courseId, List<FeedbackSessionAttributes> feedbacks,
                                        InstructorAttributes currentInstructor) {
        Iterator<FeedbackSessionAttributes> iterFs = feedbacks.iterator();
        while (iterFs.hasNext()) {
            FeedbackSessionAttributes tempFs = iterFs.next();
            if (!tempFs.courseId.equals(courseId)
              || !currentInstructor.isAllowedForPrivilege(data.student.section, tempFs.getSessionName(),
                                              Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {
                iterFs.remove();
            }
        }
    }

}
