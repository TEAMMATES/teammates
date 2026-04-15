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
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
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
                throw new UnauthorizedAccessException("User " + userInfo.getAccountId()
                        + " does not have student privileges");
            }

            if (courseId != null) {
                Course course = sqlLogic.getCourse(courseId);
                gateKeeper.verifyAccessible(sqlLogic.getStudentByAccountId(courseId, userInfo.getAccountId()), course);
            }
        } else {
            if (!userInfo.isInstructor) {
                throw new UnauthorizedAccessException("User " + userInfo.getAccountId()
                        + " does not have instructor privileges");
            }

            if (courseId != null) {
                Course course = sqlLogic.getCourse(courseId);
                gateKeeper.verifyAccessible(sqlLogic.getInstructorByAccountId(courseId, userInfo.getAccountId()), course);
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSession> feedbackSessions = new ArrayList<>();
        List<Instructor> instructors = new ArrayList<>();
        List<String> studentEmails = new ArrayList<>();
        Map<FeedbackSession, Instant> sessionToDeadline = new LinkedHashMap<>();

        if (courseId == null) {
            if (Const.EntityType.STUDENT.equals(entityType)) {
                List<Student> students = sqlLogic.getStudentsByAccountId(userInfo.getAccountId());
                for (Student student : students) {
                    String studentCourseId = student.getCourse().getId();
                    studentEmails.add(student.getEmail());
                    List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsForCourse(studentCourseId);
                    for (FeedbackSession session : sessions) {
                        sessionToDeadline.put(session, sqlLogic.getDeadlineForUser(session, student));
                    }
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = sqlLogic.getInstructorsForAccountId(userInfo.getAccountId());

                if (isInRecycleBin) {
                    feedbackSessions = sqlLogic.getSoftDeletedFeedbackSessionsForInstructors(instructors);
                } else {
                    feedbackSessions = sqlLogic.getFeedbackSessionsForInstructors(instructors);
                }
            }
        } else {
            feedbackSessions = sqlLogic.getFeedbackSessionsForCourse(courseId);
            if (Const.EntityType.STUDENT.equals(entityType) && !feedbackSessions.isEmpty()) {
                Student student = sqlLogic.getStudentByAccountId(courseId, userInfo.getAccountId());
                assert student != null;
                studentEmails.add(student.getEmail());
                for (FeedbackSession session : feedbackSessions) {
                    sessionToDeadline.put(session, sqlLogic.getDeadlineForUser(session, student));
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                instructors = Collections.singletonList(
                        sqlLogic.getInstructorByAccountId(courseId, userInfo.getAccountId()));
            }
        }

        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            for (FeedbackSession session : feedbackSessions) {
                Instructor instructor = courseIdToInstructor.get(session.getCourse().getId());
                sessionToDeadline.put(session, sqlLogic.getDeadlineForUser(session, instructor));
            }
        }

        FeedbackSessionsData responseData;
        if (Const.EntityType.STUDENT.equals(entityType)) {
            // hide sessions not visible to student
            sessionToDeadline.keySet().removeIf(session -> !session.isVisible());
        }

        responseData = new FeedbackSessionsData(sessionToDeadline);

        for (String studentEmail : studentEmails) {
            responseData.hideInformationForStudent(studentEmail);
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            responseData.getFeedbackSessions().forEach(FeedbackSessionData::hideInformation);
        } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            responseData.getFeedbackSessions().forEach(session -> {
                Instructor instructor = courseIdToInstructor.get(session.getCourseId());
                if (instructor == null) {
                    return;
                }

                InstructorPermissionSet privilege =
                        constructInstructorPrivileges(instructor, session.getFeedbackSessionName());
                session.setPrivileges(privilege);
            });
        }
        return new JsonResult(responseData);
    }
}
