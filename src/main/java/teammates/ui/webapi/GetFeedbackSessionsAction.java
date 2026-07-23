package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> courseIds = getCourseIds();
        FeedbackSessionQuery query = getFeedbackSessionQuery(courseIds);
        List<FeedbackSession> feedbackSessions = logic.getFeedbackSessions(query);

        // Admin can retrieve all feedback sessions without specific course IDs.
        if (requestContext.isAdmin() && courseIds != null) {
            return new JsonResult(new FeedbackSessionsData(feedbackSessions));
        }

        Map<String, Instructor> courseIdToInstructor = getCourseIdToInstructor(courseIds);
        Map<FeedbackSession, Instant> sessionToDeadline =
                        logic.getFeedbackSessionsWithDeadline(feedbackSessions, courseIdToInstructor);
        FeedbackSessionsData responseData = new FeedbackSessionsData(sessionToDeadline);

        responseData.getFeedbackSessions().forEach(session -> {
            Instructor instructor = courseIdToInstructor.get(session.getFeedbackSession().getCourseId());
            session.setInstructorPermissions(getPermissions(session, instructor));
        });

        return new JsonResult(responseData);
    }

    private FeedbackSessionQuery getFeedbackSessionQuery(List<String> courseIds) {
        return new FeedbackSessionQuery(
                courseIds,
                getNullableBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN).orElse(null));
    }

    private Map<String, Instructor> getCourseIdToInstructor(List<String> courseIds) {
        List<Instructor> instructors = getInstructorsForRequestedCourses(courseIds);
        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));
        return courseIdToInstructor;
    }

    private List<Instructor> getInstructorsForRequestedCourses(List<String> courseIds) {
        List<Instructor> instructors = new ArrayList<>();
        for (String courseId : courseIds) {
            Instructor instructor = getInstructorFromRequest(courseId);
            if (instructor != null) {
                instructors.add(instructor);
            }
        }
        return instructors;
    }

    private InstructorFeedbackSessionPermissionsData getPermissions(FeedbackSessionViewData feedbackSession,
            Instructor instructor) {
        if (instructor == null) {
            return new InstructorFeedbackSessionPermissionsData(false, false, false);
        }
        boolean canModifySession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor,
                        feedbackSession.getFeedbackSession().getFeedbackSessionId(),
                        Const.InstructorPermissions.CAN_SUBMIT_SESSION);
        boolean canViewSession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor,
                        feedbackSession.getFeedbackSession().getFeedbackSessionId(),
                        Const.InstructorPermissions.CAN_VIEW_SESSION);
        return new InstructorFeedbackSessionPermissionsData(
                canModifySession,
                canSubmitSession,
                canViewSession);
    }

    private List<String> getCourseIds() {
        String[] courseIds = req.getParameterValues(Const.ParamsNames.COURSE_ID);
        return courseIds == null ? null : Arrays.asList(courseIds);
    }

}
