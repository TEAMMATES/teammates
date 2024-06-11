package teammates.ui.webapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * Get a list of students.
 */
public class GetStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);

        if (isCourseMigrated(courseId)) {
            if (teamName == null) {
                // request to get all students of a course by instructor
                Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
                gateKeeper.verifyAccessible(instructor, sqlLogic.getCourse(courseId),
                        Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
            } else {
                // request to get team member by current student
                Student student = sqlLogic.getStudentByGoogleId(courseId, userInfo.id);
                if (student == null || !teamName.equals(student.getTeamName())) {
                    throw new UnauthorizedAccessException("You are not part of the team");
                }
            }
        } else {
            if (teamName == null) {
                // request to get all students of a course by instructor
                InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
                gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                        Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
            } else {
                // request to get team member by current student
                StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
                if (student == null || !teamName.equals(student.getTeam())) {
                    throw new UnauthorizedAccessException("You are not part of the team");
                }
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);

        if (isCourseMigrated(courseId)) {
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
            String privilegeName = Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS;
            boolean hasCoursePrivilege = instructor != null
                    && instructor.isAllowedForPrivilege(privilegeName);
            boolean hasSectionPrivilege = instructor != null
                    && !instructor.getSectionsWithPrivilege(privilegeName).isEmpty();

            if (teamName == null && hasCoursePrivilege) {
                // request to get all course students by instructor with course privilege
                List<Student> studentsForCourse = sqlLogic.getStudentsForCourse(courseId);

                return new JsonResult(new StudentsData(studentsForCourse));
            } else if (teamName == null && hasSectionPrivilege) {
                // request to get students by instructor with section privilege
                List<Student> studentsForCourse = sqlLogic.getStudentsForCourse(courseId);
                List<Student> studentsToReturn = new LinkedList<>();
                Set<String> sectionsWithViewPrivileges = instructor
                        .getSectionsWithPrivilege(privilegeName).keySet();

                studentsForCourse.forEach(student -> {
                    if (sectionsWithViewPrivileges.contains(student.getSectionName())) {
                        studentsToReturn.add(student);
                    }
                });

                return new JsonResult(new StudentsData(studentsToReturn));
            } else {
                // request to get team members by current student
                List<Student> studentsForTeam = sqlLogic.getStudentsByTeamName(teamName, courseId);
                StudentsData data = new StudentsData(studentsForTeam);

                data.getStudents().forEach(StudentData::hideInformationForStudent);

                return new JsonResult(data);
            }
        } else {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            String privilegeName = Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS;
            boolean hasCoursePrivilege = instructor != null
                    && instructor.isAllowedForPrivilege(privilegeName);
            boolean hasSectionPrivilege = instructor != null
                    && !instructor.getSectionsWithPrivilege(privilegeName).isEmpty();

            if (teamName == null && hasCoursePrivilege) {
                // request to get all course students by instructor with course privilege
                List<StudentAttributes> studentsForCourse = logic.getStudentsForCourse(courseId);
                StudentsData data = new StudentsData();
                List<StudentData> studentDataList = studentsForCourse
                        .stream()
                        .map(StudentData::new)
                        .collect(Collectors.toList());

                data.setStudents(studentDataList);

                return new JsonResult(data);
            } else if (teamName == null && hasSectionPrivilege) {
                // request to get students by instructor with section privilege
                List<StudentAttributes> studentsForCourse = logic.getStudentsForCourse(courseId);
                List<StudentAttributes> studentsToReturn = new LinkedList<>();
                Set<String> sectionsWithViewPrivileges = instructor.getSectionsWithPrivilege(privilegeName).keySet();
                studentsForCourse.forEach(student -> {
                    if (sectionsWithViewPrivileges.contains(student.getSection())) {
                        studentsToReturn.add(student);
                    }
                });

                StudentsData data = new StudentsData();
                List<StudentData> studentDataList = studentsToReturn
                        .stream()
                        .map(StudentData::new)
                        .collect(Collectors.toList());

                data.setStudents(studentDataList);

                return new JsonResult(data);
            } else {
                // request to get team members by current student
                List<StudentAttributes> studentsForTeam = logic.getStudentsForTeam(teamName, courseId);
                StudentsData data = new StudentsData();
                List<StudentData> studentDataList = studentsForTeam
                        .stream()
                        .map(StudentData::new)
                        .collect(Collectors.toList());

                studentDataList.forEach(StudentData::hideInformationForStudent);
                data.setStudents(studentDataList);

                return new JsonResult(data);
            }
        }
    }
}
