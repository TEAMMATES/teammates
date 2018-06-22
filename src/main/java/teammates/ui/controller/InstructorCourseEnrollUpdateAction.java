package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * Action: saving the list of edited students for a course of an instructor.
 */
public class InstructorCourseEnrollUpdateAction extends Action {

    private static final int SECTION_COLUMN_INDEX = 0;
    private static final int TEAM_COLUMN_INDEX = 1;
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int OLD_EMAIL_COLUMN_INDEX = 3;
    private static final int COMMENTS_COLUMN_INDEX = 4;
    private static final int NEW_EMAIL_COLUMN_INDEX = 5;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String updatedStudentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_UPDATED_INFO).trim();
        String sanitizedUpdatedStudentsInfo = SanitizationHelper.sanitizeForHtml(updatedStudentsInfo); // for admin message
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENTS_UPDATED_INFO, updatedStudentsInfo);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        try {
            processUpdatedStudents(courseId, updatedStudentsInfo);
        } catch (EnrollException ee) {
            setStatusForException(ee);
        }

        statusToAdmin = "Students Updated in Course <span class=\"bold\">["
                + courseId + "]:</span><br>" + sanitizedUpdatedStudentsInfo.replace("\n", "<br>");

        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }

    private void processUpdatedStudents(String courseId, String updatedStudentsInfo)
            throws EnrollException, EntityDoesNotExistException {

        if (updatedStudentsInfo.isEmpty()) {
            throw new EnrollException(Const.StatusMessages.MASS_UPDATE_LINE_EMPTY);
        }

        String[] updatedStudentLinesArray = updatedStudentsInfo.split(System.lineSeparator());
        List<String> invalidityInfo = new ArrayList<>();

        for (int i = 0; i < updatedStudentLinesArray.length; i++) {
            String line = updatedStudentLinesArray[i].trim(); // remove the line feed
            Assumption.assertNotNull(line);
            String sanitizedLine = SanitizationHelper.sanitizeForHtml(line);

            String[] columns = line.replace("|", "\t").split("\t", -1);
            String studentEmail = columns[OLD_EMAIL_COLUMN_INDEX];
            StudentAttributes student = logic.getStudentForEmail(courseId,
                    columns[OLD_EMAIL_COLUMN_INDEX]);

            if (student == null) {
                invalidityInfo.add(enrollExceptionInfo(sanitizedLine, "Student not found."));
                continue;
            }

            student.name = columns[NAME_COLUMN_INDEX];
            student.team = columns[TEAM_COLUMN_INDEX];
            student.section = columns[SECTION_COLUMN_INDEX];
            student.comments = columns[COMMENTS_COLUMN_INDEX];

            student.name = SanitizationHelper.sanitizeName(student.name);
            student.team = SanitizationHelper.sanitizeName(student.team);
            student.section = SanitizationHelper.sanitizeName(student.section);
            student.comments = SanitizationHelper.sanitizeTextField(student.comments);

            if (columns[NEW_EMAIL_COLUMN_INDEX].isEmpty()) {
                student.email = null;
            } else {
                student.email = columns[NEW_EMAIL_COLUMN_INDEX];
                student.email = SanitizationHelper.sanitizeEmail(student.email);
            }

            try {
                StudentAttributes originalStudentAttribute = logic.getStudentForEmail(courseId, studentEmail);
                student.updateWithExistingRecord(originalStudentAttribute);

                boolean isSectionChanged = student.isSectionChanged(originalStudentAttribute);
                boolean isTeamChanged = student.isTeamChanged(originalStudentAttribute);
                boolean isEmailChanged = student.isEmailChanged(originalStudentAttribute);

                if (isSectionChanged) {
                    logic.validateSectionsAndTeams(Arrays.asList(student), courseId);
                } else if (isTeamChanged) {
                    logic.validateTeams(Arrays.asList(student), courseId);
                }

                logic.updateStudent(studentEmail, student);

                if (isEmailChanged) {
                    logic.resetStudentGoogleId(student.email, courseId);
                }

            } catch (EnrollException ee) {
                invalidityInfo.add(enrollExceptionInfo(sanitizedLine, ee.getMessage()));
            } catch (InvalidParametersException ipe) {
                invalidityInfo.add(enrollExceptionInfo(sanitizedLine, ipe.getMessage()));
            }
        }

        if (!invalidityInfo.isEmpty()) {
            throw new EnrollException(StringHelper.toString(invalidityInfo, "<br>"));
        }
    }

    /**
     * Returns a {@code String} containing the enrollment exception information using the {@code errorMessage}
     * and the corresponding sanitized invalid {@code userInput}.
     */
    private String enrollExceptionInfo(String userInput, String errorMessage) {
        return String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, userInput, errorMessage);
    }
}
