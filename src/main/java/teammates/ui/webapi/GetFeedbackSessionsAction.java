package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
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

        if (Const.EntityType.STUDENT.equals(entityType)) {
            sessionToDeadline.putAll(getStudentSessionDeadlines(courseId));
        } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            instructors = getInstructors(courseId);
            feedbackSessions = getInstructorFeedbackSessions(courseId, instructors);
        } else if (courseId != null) {
            feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        }

        FeedbackSessionsData responseData;
        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            sessionToDeadline.putAll(logic.getDeadlinesForUsers(
                    mapUserToFeedbackSessions(feedbackSessions, courseIdToInstructor)));
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

    private Map<FeedbackSession, Instant> getStudentSessionDeadlines(String courseId) {
        if (courseId != null) {
            List<FeedbackSession> sessions = logic.getFeedbackSessionsForCourse(courseId);
            if (sessions.isEmpty()) {
                return Map.of();
            }

            Student student = getStudentFromRequest(courseId);
            assert student != null;
            return logic.getDeadlinesForUsers(Map.of(student, sessions));
        }

        List<Student> students = logic.getStudentsByAccountId(requestContext.getAccount().getId());
        List<String> courseIds = students.stream()
                .map(Student::getCourseId)
                .distinct()
                .toList();
        List<FeedbackSession> sessions =
                logic.getFeedbackSessionsForCoursesIncludingSoftDeletedCourses(courseIds);
        Map<String, Student> studentByCourseId = new LinkedHashMap<>();
        students.forEach(student -> studentByCourseId.put(student.getCourseId(), student));
        return logic.getDeadlinesForUsers(mapUserToFeedbackSessions(sessions, studentByCourseId));
    }

    private List<Instructor> getInstructors(String courseId) {
        if (courseId == null) {
            return logic.getInstructorsByAccountId(requestContext.getAccount().getId());
        }
        return Collections.singletonList(getInstructorFromRequest(courseId));
    }

    private List<FeedbackSession> getInstructorFeedbackSessions(
            String courseId, List<Instructor> instructors) {
        if (courseId != null) {
            return logic.getFeedbackSessionsForCourse(courseId);
        }

        boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);
        if (isInRecycleBin) {
            return logic.getSoftDeletedFeedbackSessionsForInstructors(instructors);
        }
        return logic.getFeedbackSessionsForInstructors(instructors);
    }

    private <T extends User> Map<User, List<FeedbackSession>> mapUserToFeedbackSessions(
            List<FeedbackSession> sessions, Map<String, T> userByCourseId) {
        Map<User, List<FeedbackSession>> sessionsByUser = new LinkedHashMap<>();
        for (FeedbackSession session : sessions) {
            User user = userByCourseId.get(session.getCourseId());
            if (user != null) {
                sessionsByUser.computeIfAbsent(user, ignored -> new ArrayList<>()).add(session);
            }
        }
        return sessionsByUser;
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
