package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have student privileges");
            }

            if (courseId != null) {
                if (isCourseMigrated(courseId)) {
                    Course course = sqlLogic.getCourse(courseId);
                    gateKeeper.verifyAccessible(sqlLogic.getStudentByGoogleId(courseId, userInfo.getId()), course);
                } else {
                    CourseAttributes courseAttributes = logic.getCourse(courseId);
                    gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.getId()), courseAttributes);
                }
            }
        } else {
            if (!userInfo.isInstructor) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have instructor privileges");
            }

            if (courseId != null) {
                if (isCourseMigrated(courseId)) {
                    Course course = sqlLogic.getCourse(courseId);
                    gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()), course);
                } else {
                    CourseAttributes courseAttributes = logic.getCourse(courseId);
                    gateKeeper.verifyAccessible(
                            logic.getInstructorForGoogleId(courseId, userInfo.getId()), courseAttributes);
                }
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSession> feedbackSessions = new ArrayList<>();
        List<InstructorAttributes> dataStoreInstructors = new ArrayList<>();
        List<Instructor> instructors = new ArrayList<>();
        List<FeedbackSessionAttributes> feedbackSessionAttributes = new ArrayList<>();
        List<String> studentEmails = new ArrayList<>();

        if (courseId == null) {
            if (Const.EntityType.STUDENT.equals(entityType)) {
                List<Student> students = sqlLogic.getStudentsByGoogleId(userInfo.getId());
                for (Student student : students) {
                    String studentCourseId = student.getCourse().getId();
                    String emailAddress = student.getEmail();

                    studentEmails.add(emailAddress);
                    List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsForCourse(studentCourseId);
                    feedbackSessions.addAll(sessions);
                }
                List<StudentAttributes> dataStoreStudents = logic.getStudentsForGoogleId(userInfo.getId());
                for (StudentAttributes student : dataStoreStudents) {
                    String studentCourseId = student.getCourse();
                    String emailAddress = student.getEmail();

                    studentEmails.add(emailAddress);
                    List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(studentCourseId);
                    sessions = sessions.stream()
                            .map(session -> session.getCopyForStudent(emailAddress))
                            .collect(Collectors.toList());

                    feedbackSessionAttributes.addAll(sessions);
                }
            } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = sqlLogic.getInstructorsForGoogleId(userInfo.getId());

                if (isInRecycleBin) {
                    feedbackSessions = sqlLogic.getSoftDeletedFeedbackSessionsForInstructors(instructors);
                } else {
                    feedbackSessions = sqlLogic.getFeedbackSessionsForInstructors(instructors);
                }

                dataStoreInstructors = logic.getInstructorsForGoogleId(userInfo.getId(), true);

                if (isInRecycleBin) {
                    feedbackSessionAttributes = logic.getSoftDeletedFeedbackSessionsListForInstructors(dataStoreInstructors);
                } else {
                    feedbackSessionAttributes = logic.getFeedbackSessionsListForInstructor(dataStoreInstructors);
                }
            }
        } else {
            if (isCourseMigrated(courseId)) {
                feedbackSessions = sqlLogic.getFeedbackSessionsForCourse(courseId);
                if (Const.EntityType.STUDENT.equals(entityType) && !feedbackSessions.isEmpty()) {
                    Student student = sqlLogic.getStudentByGoogleId(courseId, userInfo.getId());
                    assert student != null;
                    String emailAddress = student.getEmail();

                    studentEmails.add(emailAddress);
                } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                    instructors = Collections.singletonList(
                            sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()));
                }
            } else {
                feedbackSessionAttributes = logic.getFeedbackSessionsForCourse(courseId);
                if (Const.EntityType.STUDENT.equals(entityType) && !feedbackSessionAttributes.isEmpty()) {
                    StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
                    assert student != null;
                    String emailAddress = student.getEmail();
                    feedbackSessionAttributes = feedbackSessionAttributes.stream()
                            .map(instructorSession -> instructorSession.getCopyForStudent(emailAddress))
                            .collect(Collectors.toList());
                } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                    dataStoreInstructors =
                            Collections.singletonList(logic.getInstructorForGoogleId(courseId, userInfo.getId()));
                }
            }
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            // hide session not visible to student
            feedbackSessions = feedbackSessions.stream()
                    .filter(FeedbackSession::isVisible).collect(Collectors.toList());
            feedbackSessionAttributes = feedbackSessionAttributes.stream()
                    .filter(FeedbackSessionAttributes::isVisible).collect(Collectors.toList());
        }

        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        Map<String, InstructorAttributes> dataStoreCourseIdToInstructor = new HashMap<>();
        dataStoreInstructors.forEach(instructor -> dataStoreCourseIdToInstructor.put(instructor.getCourseId(), instructor));

        FeedbackSessionsData responseData =
                new FeedbackSessionsData(feedbackSessions, feedbackSessionAttributes);

        for (String studentEmail : studentEmails) {
            responseData.hideInformationForStudent(studentEmail);
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            responseData.getFeedbackSessions().forEach(FeedbackSessionData::hideInformationForStudent);
        } else if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            responseData.getFeedbackSessions().forEach(session -> {
                Instructor instructor = courseIdToInstructor.get(session.getCourseId());
                InstructorAttributes dataStoreInstructor = dataStoreCourseIdToInstructor.get(session.getCourseId());
                if (instructor == null && dataStoreInstructor == null) {
                    return;
                }

                if (dataStoreInstructor != null) {
                    InstructorPermissionSet privilege =
                            constructInstructorPrivileges(dataStoreInstructor, session.getFeedbackSessionName());
                    session.setPrivileges(privilege);
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
