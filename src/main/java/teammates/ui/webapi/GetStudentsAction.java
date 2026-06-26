package teammates.ui.webapi;

import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.StudentQuery;
import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Get a list of students.
 */
public class GetStudentsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        List<String> courseIds = getCourseIds();
        if (courseIds == null || courseIds.isEmpty()) {
            throw new InvalidHttpParameterException(Const.ParamsNames.COURSE_ID + " parameter is required");
        }
        for (String courseId : courseIds) {
            gateKeeper.verifyInstructorInCourse(requestContext, courseId);
        }
    }

    @Override
    public JsonResult execute() {
        StudentQuery query = new StudentQuery(
                getCourseIds(),
                getRequestParamValue(Const.ParamsNames.SEARCH_KEY),
                getLimitParamValue());

        List<Student> students = logic.getStudents(query);
        StudentsData studentsData = new StudentsData();
        if (requestContext.isAdmin()) {
            studentsData.setStudents(students.stream()
                    .map(student -> {
                        StudentData studentData = new StudentData(student);
                        studentData.addAdditionalInformationForAdmin(student.getAccountId());
                        return studentData;
                    })
                    .toList());
        } else {
            studentsData.setStudents(students.stream()
                    .map(StudentData::new)
                    .toList());
        }
        return new JsonResult(studentsData);
    }

    private List<String> getCourseIds() {
        String[] courseIds = req.getParameterValues(Const.ParamsNames.COURSE_ID);
        return courseIds == null ? null : Arrays.asList(courseIds);
    }

}
