package teammates.ui.controller;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorCourseEditSaveAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        // Restore previous attributes of the course
        CourseAttributes course = logic.getCourse(courseId);
        String prevCourseName = course.getName();
        String prevCourseTimeZone = course.getTimeZone().toString();

        String courseName = getRequestParamValue(Const.ParamsNames.COURSE_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_NAME, courseName);

        String courseTimeZone = getRequestParamValue(Const.ParamsNames.COURSE_TIME_ZONE);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);

        try {
            logic.updateCourse(courseId, courseName, courseTimeZone);

            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_EDITED, StatusMessageColor.SUCCESS));

            StringBuilder courseStatus = new StringBuilder(100);
            Boolean isCourseChanged = false;
            if (!courseName.equals(prevCourseName)) {
                String nameEdit = "Course name for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                                        + "Old name: " + prevCourseName + "<br>"
                                        + "New name: " + courseName + "<br><br>";
                courseStatus.append(nameEdit);
                isCourseChanged = true;
            }
            if (!courseTimeZone.equals(prevCourseTimeZone)) {
                String timeZoneEdit = "Time zone for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                                            + "Old time zone: " + prevCourseTimeZone + "<br>"
                                            + "New time zone: " + courseTimeZone;
                courseStatus.append(timeZoneEdit);
                isCourseChanged = true;
            }

            if (!isCourseChanged) {
                courseStatus.append("Nothing edited for Course <span class=\"bold\">[" + courseId + "]</span>");
            }
            statusToAdmin = courseStatus.toString();

        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }

        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }
}
