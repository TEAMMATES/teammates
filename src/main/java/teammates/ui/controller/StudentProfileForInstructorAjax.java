package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

public class StudentProfileForInstructorAjax extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        ActionResult result = null;
        if (getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL) != null &&
                getRequestParamValue(Const.ParamsNames.COURSE_ID) != null) {
            result = handleRequestWithEmailAndCourse();
            statusToAdmin = "Requested Profile Picture by instructor/other students";
        } else {
            Assumption.fail("expected student email with courseId");
        }
        
        return result;
    }

    private ActionResult handleRequestWithEmailAndCourse() throws EntityDoesNotExistException {
        String email = getStudentEmailFromRequest();
        String courseId = getCourseIdFromRequest();
        log.info("email: " + email + ", course: " + courseId);
        
        StudentAttributes student = getStudentForGivenParameters(courseId, email);
        new GateKeeper().verifyAccessibleForCurrentUserAsInstructor(account, courseId, student.section);
        
        StudentProfileAttributes profile = logic.getStudentProfile(student.googleId);
        fillInEmptyNameAndEmail(profile, student);
        
        PageData data = new StudentProfileForInstructorAjaxPageData(account, profile);
        
        return createAjaxResult("", data);
    }

    private void fillInEmptyNameAndEmail(StudentProfileAttributes profile, StudentAttributes student) {
        if (profile.email.isEmpty()) {
            profile.email = student.email;
        }
        if (profile.shortName.isEmpty()) {
            profile.shortName = student.name;
        }        
    }

    private StudentAttributes getStudentForGivenParameters(String courseId,
            String email) throws EntityDoesNotExistException {
        StudentAttributes student = logic.getStudentForEmail(courseId, email);
        if (student == null) {
            throw new EntityDoesNotExistException("student with " +
                    courseId + "/" + email);
        }
        return student;
    }

    private String getCourseIdFromRequest() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        courseId = StringHelper.decrypt(courseId);
        return courseId;
    }

    private String getStudentEmailFromRequest() {
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, email);
        email = StringHelper.decrypt(email);
        return email;
    }

}
