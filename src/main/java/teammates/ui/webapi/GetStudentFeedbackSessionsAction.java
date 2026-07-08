package teammates.ui.webapi;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Get a list of feedback sessions for a student in a course.
 */
public class GetStudentFeedbackSessionsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyStudentInCourse(requestContext, courseId);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Student student = getStudentFromRequest(courseId);
        List<FeedbackSession> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        Map<FeedbackSession, Instant> sessionToDeadline = new LinkedHashMap<>();

        for (FeedbackSession session : feedbackSessions) {
            if (session.isVisible()) {
                sessionToDeadline.put(session, logic.getDeadlineForUser(session, student));
            }
        }

        FeedbackSessionsData responseData = new FeedbackSessionsData(sessionToDeadline);
        responseData.getFeedbackSessions().forEach(session -> session.getFeedbackSession().hideInformation());
        return new JsonResult(responseData);
    }
}
