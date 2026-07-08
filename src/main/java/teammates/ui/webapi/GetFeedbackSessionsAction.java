package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackSessionQuery;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;

/**
 * Get a list of feedback sessions.
 */
public class GetFeedbackSessionsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        List<String> courseIds = getCourseIds();
        if (courseIds == null || courseIds.isEmpty()) {
            throw new InvalidHttpParameterException(Const.ParamsNames.COURSE_ID + " parameter is required");
        }

        for (String courseId : courseIds) {
            gateKeeper.verifyInstructorInCourse(requestContext, courseId);
        }
    }

    @Override
    public JsonResult execute() {
        List<FeedbackSession> feedbackSessions = getFeedbackSessions();
        if (requestContext.isAdmin()) {
            return new JsonResult(new FeedbackSessionsData(feedbackSessions));
        }

        List<Instructor> instructors = getInstructorsForRequestedCourses(getCourseIds());
        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        Map<FeedbackSession, Instant> sessionToDeadline = new LinkedHashMap<>();
        for (FeedbackSession session : feedbackSessions) {
            Instructor instructor = courseIdToInstructor.get(session.getCourseId());
            sessionToDeadline.put(session, logic.getDeadlineForUser(session, instructor));
        }

        FeedbackSessionsData responseData = new FeedbackSessionsData(sessionToDeadline);
        responseData.getFeedbackSessions().forEach(session -> {
            InstructorFeedbackSessionPermissionsData permissionsData =
                    getPermissionsData(courseIdToInstructor, session);
            session.setInstructorPermissions(permissionsData);
        });
        return new JsonResult(responseData);
    }

    private List<FeedbackSession> getFeedbackSessions() {
        FeedbackSessionQuery query = new FeedbackSessionQuery(
                getCourseIds(),
                getNullableBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN).orElse(null));
        return logic.getFeedbackSessions(query);
    }

    private List<Instructor> getInstructorsForRequestedCourses(List<String> courseIds) {
        List<Instructor> instructors = new ArrayList<>();
        for (String courseId : courseIds) {
            instructors.add(getInstructorFromRequest(courseId));
        }
        return instructors;
    }

    private List<String> getCourseIds() {
        String[] courseIds = req.getParameterValues(Const.ParamsNames.COURSE_ID);
        return courseIds == null ? null : Arrays.asList(courseIds);
    }

    private InstructorFeedbackSessionPermissionsData getPermissionsData(Map<String, Instructor> courseIdToInstructor,
            FeedbackSessionViewData session) {
        Instructor instructor = courseIdToInstructor.get(session.getFeedbackSession().getCourseId());
        if (instructor == null) {
            return new InstructorFeedbackSessionPermissionsData(false, false, false);
        }
        UUID sessionId = session.getFeedbackSession().getFeedbackSessionId();
        boolean canModifySession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, sessionId,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION);
        boolean canViewSession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, sessionId,
                Const.InstructorPermissions.CAN_VIEW_SESSION);
        return new InstructorFeedbackSessionPermissionsData(canModifySession,
                canSubmitSession, canViewSession);
    }
}
