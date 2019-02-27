package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets details of a course in from an instructor.
 */
public class GetInstructorCourseDetailsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("No course with given instructor is found."));
        }

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));
    }

    @Override
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseDetailsBundle courseDetails;
        CourseInfo output;

        try {
            courseDetails = logic.getCourseDetails(courseId);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);

            output = new CourseInfo(instructor, courseDetails, instructors);

        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No course with given instructor is found.", HttpStatus.SC_NOT_FOUND);
        }

        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetInstructorCourseDetailsAction}.
     */
    public static class CourseInfo extends ApiOutput {
        private final CourseDetailsBundle courseDetails;
        private final InstructorAttributes currentInstructor;
        private final List<InstructorAttributes> instructors;
        private final List<StudentListSectionData> sections;
        private final boolean hasSection;

        public CourseInfo(InstructorAttributes currentInstructor, CourseDetailsBundle courseDetails,
                          List<InstructorAttributes> instructors) {
            this.currentInstructor = currentInstructor;
            this.courseDetails = courseDetails;
            this.instructors = instructors;

            this.sections = new ArrayList<>();
            for (SectionDetailsBundle section : courseDetails.sections) {
                boolean isAllowedToViewStudentInSection = currentInstructor.isAllowedForPrivilege(section.name,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
                boolean isAllowedToModifyStudent = currentInstructor.isAllowedForPrivilege(section.name,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

                for (TeamDetailsBundle teamDetails : section.teams) {
                    sections.add(new StudentListSectionData(section.name, isAllowedToViewStudentInSection,
                            isAllowedToModifyStudent, createStudentDataInSection(section.name, teamDetails.students)));
                }
            }

            if (sections.size() == 1) {
                StudentListSectionData section = sections.get(0);
                this.hasSection = !Const.DEFAULT_SECTION.equals(section.sectionName);
            } else {
                this.hasSection = true;
            }
        }

        public CourseDetailsBundle getCourseDetails() {
            return courseDetails;
        }

        public InstructorAttributes getCurrentInstructor() {
            return currentInstructor;
        }

        public List<InstructorAttributes> getInstructors() {
            return instructors;
        }

        public List<StudentListSectionData> getSections() {
            return sections;
        }

        public boolean isHasSection() {
            return hasSection;
        }

        private List<StudentListStudentData> createStudentDataInSection(String sectionName,
                                                                        List<StudentAttributes> studentsInCourse) {
            return studentsInCourse.stream().filter(student -> student.section.equals(sectionName))
                    .map(student -> new StudentListStudentData(
                            student.name, student.email, student.getStudentStatus(), student.team))
                    .collect(Collectors.toList());
        }

        private static class StudentListSectionData {
            public String sectionName;
            public boolean isAllowedToViewStudentInSection;
            public boolean isAllowedToModifyStudent;
            public List<StudentListStudentData> students;

            StudentListSectionData(String sectionName, boolean isAllowedToViewStudentInSection,
                                   boolean isAllowedToModifyStudent, List<StudentListStudentData> students) {
                this.sectionName = sectionName;
                this.isAllowedToViewStudentInSection = isAllowedToViewStudentInSection;
                this.isAllowedToModifyStudent = isAllowedToModifyStudent;
                this.students = students;
            }
        }

        private static class StudentListStudentData {

            public String name;
            public String email;
            public String status;
            public String team;

            StudentListStudentData(String studentName, String studentEmail, String studentStatus, String teamName) {
                this.name = studentName;
                this.email = studentEmail;
                this.status = studentStatus;
                this.team = teamName;
            }
        }
    }
}
