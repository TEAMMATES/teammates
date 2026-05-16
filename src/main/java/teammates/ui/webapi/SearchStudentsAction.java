package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Action for searching for students.
 */
public class SearchStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Only instructors and admins can search for student
        if (!authContext.isInstructor() && !authContext.isAdmin()) {
            throw new UnauthorizedAccessException("Instructor or Admin privilege is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        String entity = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<Student> students;

        if (authContext.isInstructor() && Const.EntityType.INSTRUCTOR.equals(entity)) {
            List<Instructor> instructors = logic.getInstructorsForGoogleId(authContext.id());
            students = logic.searchStudents(searchKey, instructors);
        } else if (authContext.isAdmin() && Const.EntityType.ADMIN.equals(entity)) {
            students = logic.searchStudentsInWholeSystem(searchKey);
        } else {
            throw new InvalidHttpParameterException("Invalid entity type for search");
        }

        List<StudentData> studentDataList = new ArrayList<>();
        for (Student s : students) {
            StudentData studentData = new StudentData(s);

            if (authContext.isAdmin() && Const.EntityType.ADMIN.equals(entity)) {
                studentData.addAdditionalInformationForAdminSearch(
                        s.getRegKey(),
                        s.getGoogleId()
                );
            }

            studentDataList.add(studentData);
        }
        StudentsData studentsData = new StudentsData();
        studentsData.setStudents(studentDataList);

        return new JsonResult(studentsData);
    }
}
