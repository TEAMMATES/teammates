package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.StudentData;

/**
 * Get the information of the student associated with the request.
 */
public class GetOwnStudentAction extends RegKeyAction {
    @Override
    void checkSpecificAccessControl() {
        // No specific access control required.
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Student student = getStudentFromRequest(courseId);
        if (student == null) {
            throw new EntityNotFoundException("Student could not be found for this course");
        }

        StudentData studentData = new StudentData(student);
        studentData.hideInformationForStudent();
        return new JsonResult(studentData);
    }
}
