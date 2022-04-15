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
import teammates.ui.output.FeedbackSessionsData;

/**
 * Get a list of feedback sessions.
 */
class GetFeedbackSessionsAction extends Action {

    private static final String NO_SUCH_USER_FOUND = "No such user found.";

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

        if (!(entityType.equals(Const.EntityType.STUDENT) || entityType.equals(Const.EntityType.INSTRUCTOR))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (entityType.equals(Const.EntityType.STUDENT)) {
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have student privileges");
            }

            if (courseId != null) {
                CourseAttributes courseAttributes = logic.getCourse(courseId);
                gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.getId()), courseAttributes);
            }
        } else {
            if (!userInfo.isInstructor) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have instructor privileges");
            }

            if (courseId != null) {
                CourseAttributes courseAttributes = logic.getCourse(courseId);
                gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()), courseAttributes);
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSessionAttributes> feedbackSessionAttributes;
        List<StudentAttributes> students = new ArrayList<>();
        List<InstructorAttributes> instructors = new ArrayList<>();

        if (courseId == null) {
            if (entityType.equals(Const.EntityType.STUDENT)) {
                students = logic.getStudentsForGoogleId(userInfo.getId());
                feedbackSessionAttributes = new ArrayList<>();
                for (StudentAttributes student : students) {
                    feedbackSessionAttributes.addAll(logic.getFeedbackSessionsForCourse(student.getCourse()));
                }
                if (students.isEmpty()) {
                    throw new EntityNotFoundException(NO_SUCH_USER_FOUND);
                }
                String emailAddress = students.get(0).getEmail();
                feedbackSessionAttributes = feedbackSessionAttributes.stream()
                        .map(instructorSession -> instructorSession.sanitizeForStudent(emailAddress))
                        .collect(Collectors.toList());
            } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = logic.getInstructorsForGoogleId(userInfo.getId(), true);

                if (isInRecycleBin) {
                    feedbackSessionAttributes = logic.getSoftDeletedFeedbackSessionsListForInstructors(instructors);
                } else {
                    feedbackSessionAttributes = logic.getFeedbackSessionsListForInstructor(instructors);
                }
                if (instructors.isEmpty()) {
                    throw new EntityNotFoundException(NO_SUCH_USER_FOUND);
                }
                String emailAddress = instructors.get(0).getEmail();
                feedbackSessionAttributes = feedbackSessionAttributes.stream()
                        .map(instructorSession -> instructorSession.sanitizeForInstructor(emailAddress))
                        .collect(Collectors.toList());
            } else {
                feedbackSessionAttributes = new ArrayList<>();
            }
        } else {
            feedbackSessionAttributes = logic.getFeedbackSessionsForCourse(courseId);
            if (entityType.equals(Const.EntityType.STUDENT) && !feedbackSessionAttributes.isEmpty()) {
                StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
                students = Collections.singletonList(student);
                if (student == null) {
                    throw new EntityNotFoundException(NO_SUCH_USER_FOUND);
                }
                String emailAddress = student.getEmail();
                feedbackSessionAttributes = feedbackSessionAttributes.stream()
                        .map(instructorSession -> instructorSession.sanitizeForStudent(emailAddress))
                        .collect(Collectors.toList());
            } else if (entityType.equals(Const.EntityType.INSTRUCTOR) && !feedbackSessionAttributes.isEmpty()) {
                InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
                instructors = Collections.singletonList(instructor);
                if (instructor == null) {
                    throw new EntityNotFoundException(NO_SUCH_USER_FOUND);
                }
                String emailAddress = instructor.getEmail();
                feedbackSessionAttributes = feedbackSessionAttributes.stream()
                        .map(instructorSession -> instructorSession.sanitizeForInstructor(emailAddress))
                        .collect(Collectors.toList());
            }
        }

        if (entityType.equals(Const.EntityType.STUDENT)) {
            // hide session not visible to student
            feedbackSessionAttributes = feedbackSessionAttributes.stream()
                    .filter(FeedbackSessionAttributes::isVisible).collect(Collectors.toList());
        }

        Map<String, InstructorAttributes> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        FeedbackSessionsData responseData = new FeedbackSessionsData(feedbackSessionAttributes);
        if (entityType.equals(Const.EntityType.STUDENT)) {
            String emailAddress = students.isEmpty() ? null : students.get(0).getEmail();
            responseData.getFeedbackSessions().forEach(session -> {
                session.hideInformationForStudent();
                if (emailAddress == null) {
                    return;
                }
                session.filterDeadlinesForStudent(emailAddress);
            });
        } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            responseData.getFeedbackSessions().forEach(session -> {
                InstructorAttributes instructor = courseIdToInstructor.get(session.getCourseId());
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
