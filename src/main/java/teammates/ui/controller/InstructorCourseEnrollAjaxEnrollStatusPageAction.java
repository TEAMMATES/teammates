package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;

/**
 * Action: returns list of current students in the course through AJAX.
 */
public class InstructorCourseEnrollAjaxEnrollStatusPageAction extends Action {

    private static Map<String, String> enrollErrorLines;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);
        String sanitizedStudentsInfo = SanitizationHelper.sanitizeForHtml(studentsInfo);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, studentsInfo);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        try {
            enrollErrorLines = new HashMap<>();
            processEnrollStatus(studentsInfo, courseId);

            InstructorCourseEnrollPageData pageData =
                    new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);

            statusToUser.add(new StatusMessage("Enroll data verified.", StatusMessageColor.SUCCESS));
            return createAjaxResult(pageData);
        } catch (EnrollException e) {
            InstructorCourseEnrollPageData pageData =
                    new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);

            List<String> exceptionMessages = new ArrayList<>(Arrays.asList(e.getMessage().split("<br>")));

            for (String exceptionMessage : exceptionMessages) {
                if (exceptionMessage.equals(Const.StatusMessages.ENROLL_LINE_EMPTY)) {
                    statusToUser.add(new StatusMessage(Const.StatusMessages.ENROLL_LINE_EMPTY,
                            StatusMessageColor.DANGER));
                    break;
                } else if (exceptionMessage.equals(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED)) {
                    statusToUser.add(new StatusMessage(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED,
                            StatusMessageColor.DANGER));
                    break;
                }

                if (!"Please use the enroll page to edit multiple students".equals(exceptionMessage)) {
                    enrollErrorLines.put(exceptionMessage.split("##")[0], exceptionMessage.split("##")[1]);
                }
            }
            pageData.setEnrollErrorLines(enrollErrorLines);
            pageData.setStatusMessagesToUser(statusToUser);
            statusToAdmin += "<br>Enrollment string entered by user:<br>" + sanitizedStudentsInfo.replace("\n", "<br>");

            return createAjaxResult(pageData);
        }
    }

    private void processEnrollStatus(String studentsInfo, String courseId)
            throws EnrollException, EntityDoesNotExistException {
        logic.verifyEnrollStatus(studentsInfo, courseId);
    }
}
