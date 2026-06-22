package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
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

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId,
                Const.InstructorPermissions.CAN_VIEW_STUDENT);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = requestContext.isAdmin()
                ? null
                : getInstructorFromRequest(courseId);
        String privilegeName = Const.InstructorPermissions.CAN_VIEW_STUDENT;
        boolean hasCoursePrivilege = instructor != null
                && logic.hasInstructorPermissions(instructor, privilegeName);
        boolean hasSectionPrivilege = instructor != null
                && !logic.getSectionsWithInstructorPermission(instructor, privilegeName).isEmpty();

        if (requestContext.isAdmin() || hasCoursePrivilege) {
            List<Student> studentsForCourse = logic.getStudentsForCourse(courseId);

            return new JsonResult(new StudentsData(studentsForCourse));
        } else if (hasSectionPrivilege) {
            List<Student> studentsForCourse = logic.getStudentsForCourse(courseId);
            List<Student> studentsToReturn = new LinkedList<>();
            Set<UUID> sectionsWithViewPrivileges =
                    logic.getSectionsWithInstructorPermission(instructor, privilegeName).keySet();

            studentsForCourse.forEach(student -> {
                if (sectionsWithViewPrivileges.contains(student.getSectionId())) {
                    studentsToReturn.add(student);
                }
            });

            return new JsonResult(new StudentsData(studentsToReturn));
        } else {
            return new JsonResult(new StudentsData(List.of()));
        }
    }
}
