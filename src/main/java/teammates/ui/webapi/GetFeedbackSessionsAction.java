package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.output.InstructorFeedbackSessionPermissionsData;

/**
 * Get a list of feedback sessions.
 */
public class GetFeedbackSessionsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(Const.EntityType.STUDENT.equals(entityType) || Const.EntityType.INSTRUCTOR.equals(entityType))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (Const.EntityType.STUDENT.equals(entityType)) {
            if (courseId != null) {
                gateKeeper.verifyStudentInCourse(requestContext, courseId);
            }
        } else {
            if (courseId != null) {
                gateKeeper.verifyInstructorInCourse(requestContext, courseId);
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSession> feedbackSessions = new ArrayList<>();
        List<Instructor> instructors = new ArrayList<>();
        Map<FeedbackSession, Instant> sessionToDeadline = new LinkedHashMap<>();

        if (courseId == null) {
            if (Const.EntityType.STUDENT.equals(entityType)) {
                List<Student> students = logic.getStudentsByAccountId(requestContext.getAccount().getId());
                for (Student student : students) {
                    String studentCourseId = student.getCourseId();
                    List<FeedbackSession> sessions = logic.getFeedbackSessionsForCourse(studentCourseId);
                    for (FeedbackSession session : sessions) {
                        sessionToDeadline.put(session, logic.getDeadlineForUser(session, student));
                    }
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = logic.getInstructorsByAccountId(requestContext.getAccount().getId());

                if (isInRecycleBin) {
                    feedbackSessions = logic.getSoftDeletedFeedbackSessionsForInstructors(instructors);
                } else {
                    feedbackSessions = logic.getFeedbackSessionsForInstructors(instructors);
                }
            }
        } else {
            feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
            if (Const.EntityType.STUDENT.equals(entityType) && !feedbackSessions.isEmpty()) {
                Student student = getStudentFromRequest(courseId);
                assert student != null;
                for (FeedbackSession session : feedbackSessions) {
                    sessionToDeadline.put(session, logic.getDeadlineForUser(session, student));
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                instructors = Collections.singletonList(
                        getInstructorFromRequest(courseId));
            }
        }

        FeedbackSessionsData responseData;
        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            for (FeedbackSession session : feedbackSessions) {
                Instructor instructor = courseIdToInstructor.get(session.getCourseId());
                sessionToDeadline.put(session, logic.getDeadlineForUser(session, instructor));
            }
            responseData = new FeedbackSessionsData(sessionToDeadline);
            responseData.getFeedbackSessions().forEach(session -> {
                var permissionsData = getPermissionsData(courseIdToInstructor, session);
                session.setInstructorPermissions(permissionsData);
            });
        } else if (Const.EntityType.STUDENT.equals(entityType)) {
            // hide sessions not visible to student
            sessionToDeadline.keySet().removeIf(session -> !session.isVisible());
            responseData = new FeedbackSessionsData(sessionToDeadline);
            responseData.getFeedbackSessions().forEach(session -> session.getFeedbackSession().hideInformation());
        } else {
            responseData = new FeedbackSessionsData(feedbackSessions);
        }

        return new JsonResult(responseData);
    }

    private InstructorFeedbackSessionPermissionsData getPermissionsData(Map<String, Instructor> courseIdToInstructor,
            FeedbackSessionViewData session) {
        Instructor instructor = courseIdToInstructor.get(session.getFeedbackSession().getCourseId());
        if (instructor == null) {
            return new InstructorFeedbackSessionPermissionsData(false, false, false);
        }
        String sessionName = session.getFeedbackSession().getFeedbackSessionName();
        boolean canModifySession = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
        boolean canSubmitSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, sessionName,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        boolean canViewSessionInSections = logic.hasInstructorPermissions(instructor,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS)
                || logic.hasInstructorPermissionsForSectionInAnySection(instructor, sessionName,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        return new InstructorFeedbackSessionPermissionsData(canModifySession,
                canSubmitSessionInSections, canViewSessionInSections);
    }
}
