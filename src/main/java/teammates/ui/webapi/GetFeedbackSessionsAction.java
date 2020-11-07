package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.output.InstructorPrivilegeData;

/**
 * Get a list of feedback sessions.
 */
class GetFeedbackSessionsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
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
    JsonResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSessionAttributes> feedbackSessionAttributes;
        List<InstructorAttributes> instructors = new ArrayList<>();

        if (courseId == null) {
            if (entityType.equals(Const.EntityType.STUDENT)) {
                List<StudentAttributes> students = logic.getStudentsForGoogleId(userInfo.getId());
                feedbackSessionAttributes = new ArrayList<>();
                for (StudentAttributes student : students) {
                    feedbackSessionAttributes.addAll(logic.getFeedbackSessionsForCourse(student.getCourse()));
                }
            } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = logic.getInstructorsForGoogleId(userInfo.getId(), true);

                if (isInRecycleBin) {
                    feedbackSessionAttributes = logic.getSoftDeletedFeedbackSessionsListForInstructors(instructors);
                } else {
                    feedbackSessionAttributes = logic.getFeedbackSessionsListForInstructor(instructors);
                }
            } else {
                feedbackSessionAttributes = new ArrayList<>();
            }
        } else {
            feedbackSessionAttributes = logic.getFeedbackSessionsForCourse(courseId);
            if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
                instructors = Collections.singletonList(logic.getInstructorForGoogleId(courseId, userInfo.getId()));
            }
        }

        if (entityType.equals(Const.EntityType.STUDENT)) {
            // hide session not visible to student
            feedbackSessionAttributes = feedbackSessionAttributes.stream()
                    .filter(FeedbackSessionAttributes::isVisible).collect(Collectors.toList());
        }

        Map<String, InstructorAttributes> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.courseId, instructor));

        FeedbackSessionsData responseData = new FeedbackSessionsData(feedbackSessionAttributes);
        if (entityType.equals(Const.EntityType.STUDENT)) {
            responseData.getFeedbackSessions().forEach(FeedbackSessionData::hideInformationForStudent);
        } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            responseData.getFeedbackSessions().forEach(session -> {
                InstructorAttributes instructor = courseIdToInstructor.get(session.getCourseId());
                if (instructor == null) {
                    return;
                }

                InstructorPrivilegeData privilege =
                        constructInstructorPrivileges(instructor, session.getFeedbackSessionName());
                session.setPrivileges(privilege);
            });
        }
        return new JsonResult(responseData);
    }

}
