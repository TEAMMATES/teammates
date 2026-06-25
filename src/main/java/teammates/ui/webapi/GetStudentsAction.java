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

        // Any instructor can access the list of students,
        // but the list will be filtered to only include students visible to them.
        gateKeeper.verifyInstructorInAnyCourse(requestContext);
    }

    @Override
    public JsonResult execute() {
        StudentQuery query = new StudentQuery(
                getCourseIds(),
                getRequestParamValue(Const.ParamsNames.SEARCH_KEY),
                getNullablePositiveIntRequestParamValue(Const.ParamsNames.LIMIT));

        if (requestContext.isAdmin()) {
            List<Student> students = logic.getStudents(query);
            StudentsData studentsData = new StudentsData();
            studentsData.setStudents(students.stream()
                    .map(student -> {
                        StudentData studentData = new StudentData(student);
                        studentData.addAdditionalInformationForAdmin(student.getAccountId());
                        return studentData;
                    })
                    .toList());
            return new JsonResult(studentsData);
        }

        List<Student> students = logic.getStudentsVisibleToAccount(query, requestContext.getAccount());
        StudentsData studentsData = new StudentsData();
        studentsData.setStudents(students.stream()
                .map(StudentData::new)
                .toList());
        return new JsonResult(studentsData);
    }

    private List<String> getCourseIds() {
        String[] courseIds = req.getParameterValues(Const.ParamsNames.COURSE_ID);
        return courseIds == null ? null : Arrays.asList(courseIds);
    }

    private Integer getNullablePositiveIntRequestParamValue(String paramName) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return null;
        }

        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected integer value for " + paramName + " parameter, but found: [" + value + "]", e);
        }
        if (parsed <= 0) {
            throw new InvalidHttpParameterException(
                    "Expected positive integer value for " + paramName + " parameter, but found: [" + value + "]");
        }
        return parsed;
    }
}
