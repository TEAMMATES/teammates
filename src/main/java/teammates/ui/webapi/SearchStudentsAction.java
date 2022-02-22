package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Action for searching for students.
 */
class SearchStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Only instructors and admins can search for student
        if (!userInfo.isInstructor && !userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Instructor or Admin privilege is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        String entity = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        List<StudentAttributes> students;

        try {
            if (userInfo.isInstructor && entity.equals(Const.EntityType.INSTRUCTOR)) {
                List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
                students = logic.searchStudents(searchKey, instructors);
            } else if (userInfo.isAdmin && entity.equals(Const.EntityType.ADMIN)) {
                students = logic.searchStudentsInWholeSystem(searchKey);
            } else {
                throw new InvalidHttpParameterException("Invalid entity type for search");
            }
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        List<StudentData> studentDataList = new ArrayList<>();
        for (StudentAttributes s : students) {
            StudentData studentData = new StudentData(s);

            if (userInfo.isAdmin && entity.equals(Const.EntityType.ADMIN)) {
                studentData.addAdditionalInformationForAdminSearch(
                        s.getKey(),
                        logic.getCourseInstitute(s.getCourse()),
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
