package teammates.ui.newcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.template.StudentListSectionData;

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

            output = new CourseInfo(instructor, courseDetails, instructors, userInfo.id);

            String courseStudentListAsCsv = logic.getCourseStudentListAsCsv(courseId, userInfo.id);
            output.setStudentListHtmlTableAsString(StringHelper.csvToHtmlTable(courseStudentListAsCsv));

        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No course with given instructor is found.", HttpStatus.SC_NOT_FOUND);
        }

        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetInstructorCourseDetailsAction}.
     */
    public static class CourseInfo extends ActionResult.ActionOutput {
        private final CourseDetailsBundle courseDetails;
        private final InstructorAttributes currentInstructor;
        private final List<InstructorAttributes> instructors;
        private final List<StudentListSectionData> sections;
        private final boolean hasSection;
        private String studentListHtmlTableAsString;

        public CourseInfo(InstructorAttributes currentInstructor, CourseDetailsBundle courseDetails,
                          List<InstructorAttributes> instructors, String userId) {
            this.currentInstructor = currentInstructor;
            this.courseDetails = courseDetails;
            this.instructors = instructors;

            this.sections = new ArrayList<>();
            for (SectionDetailsBundle section : courseDetails.sections) {
                Map<String, String> emailPhotoUrlMapping = new HashMap<>();
                for (TeamDetailsBundle teamDetails : section.teams) {
                    for (StudentAttributes student : teamDetails.students) {
                        String studentPhotoUrl = student.getPublicProfilePictureUrl();
                        studentPhotoUrl = Url.addParamToUrl(studentPhotoUrl, Const.ParamsNames.USER_ID, userId);
                        emailPhotoUrlMapping.put(student.email, studentPhotoUrl);
                    }
                }
                boolean isAllowedToViewStudentInSection = currentInstructor.isAllowedForPrivilege(section.name,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
                boolean isAllowedToModifyStudent = currentInstructor.isAllowedForPrivilege(section.name,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
                sections.add(new StudentListSectionData(section, isAllowedToViewStudentInSection,
                        isAllowedToModifyStudent, emailPhotoUrlMapping, userId, "",
                        Const.PageNames.INSTRUCTOR_COURSE_DETAILS_PAGE));
            }

            if (sections.size() == 1) {
                StudentListSectionData section = sections.get(0);
                this.hasSection = !"None".equals(section.getSectionName());
            } else {
                this.hasSection = true;
            }
        }

        private void setStudentListHtmlTableAsString(String studentListHtmlTableAsString) {
            this.studentListHtmlTableAsString = studentListHtmlTableAsString;
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

        public String getStudentListHtmlTableAsString() {
            return studentListHtmlTableAsString;
        }
    }
}
