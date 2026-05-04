package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.util.Const;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionsData;

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
        if (userInfo.isAdmin) {
            return;
        }

        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(Const.EntityType.STUDENT.equals(entityType) || Const.EntityType.INSTRUCTOR.equals(entityType))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (Const.EntityType.STUDENT.equals(entityType)) {
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have student privileges");
            }

            if (courseId != null) {
                Course course = logic.getCourse(courseId);
                gateKeeper.verifyAccessible(logic.getStudentByGoogleId(courseId, userInfo.getId()), course);
            }
        } else {
            if (!userInfo.isInstructor) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have instructor privileges");
            }

            if (courseId != null) {
                Course course = logic.getCourse(courseId);
                gateKeeper.verifyAccessible(logic.getInstructorByGoogleId(courseId, userInfo.getId()), course);
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
                List<Student> students = logic.getStudentsByGoogleId(userInfo.getId());
                for (Student student : students) {
                    String studentCourseId = student.getCourseId();
                    List<FeedbackSession> sessions = logic.getFeedbackSessionsForCourse(studentCourseId);
                    for (FeedbackSession session : sessions) {
                        sessionToDeadline.put(session, logic.getDeadlineForUser(session, student));
                    }
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = logic.getInstructorsForGoogleId(userInfo.getId());

                if (isInRecycleBin) {
                    feedbackSessions = logic.getSoftDeletedFeedbackSessionsForInstructors(instructors);
                } else {
                    feedbackSessions = logic.getFeedbackSessionsForInstructors(instructors);
                }
            }
        } else {
            feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
            if (Const.EntityType.STUDENT.equals(entityType) && !feedbackSessions.isEmpty()) {
                Student student = logic.getStudentByGoogleId(courseId, userInfo.getId());
                assert student != null;
                for (FeedbackSession session : feedbackSessions) {
                    sessionToDeadline.put(session, logic.getDeadlineForUser(session, student));
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                instructors = Collections.singletonList(
                        logic.getInstructorByGoogleId(courseId, userInfo.getId()));
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
                Instructor instructor = courseIdToInstructor.get(session.getCourseId());
                if (instructor == null) {
                    return;
                }

                InstructorPermissionSet privilege =
                        constructInstructorPrivileges(instructor, session.getFeedbackSessionName());
                session.setPrivileges(privilege);
            });
        } else if (Const.EntityType.STUDENT.equals(entityType)) {
            // hide sessions not visible to student
            sessionToDeadline.keySet().removeIf(session -> !session.isVisible());
            responseData = new FeedbackSessionsData(sessionToDeadline);
            responseData.getFeedbackSessions().forEach(FeedbackSessionData::hideInformation);
        } else {
            responseData = new FeedbackSessionsData(feedbackSessions);
        }

        return new JsonResult(responseData);
    }
}
