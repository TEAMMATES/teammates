package teammates.ui.newcontroller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Gets information related to a specific course for instructor course edit page.
 */
public class GetCourseEditDetailsAction extends Action {

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
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        if (instructor == null) {
            throw new UnauthorizedAccessException("No instructor " + userInfo.id + " with access to given course");
        }

        CourseAttributes courseToEdit = logic.getCourse(courseId);
        if (courseToEdit == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("No course with id: " + courseId));
        }

        gateKeeper.verifyAccessible(instructor, courseToEdit);
    }

    @Override
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String index = getRequestParamValue(Const.ParamsNames.COURSE_EDIT_MAIN_INDEX);

        List<InstructorAttributes> instructorList = new ArrayList<>();
        int instructorToShowIndex = -1; // -1 means showing all instructors

        if (instructorEmail == null) {
            instructorList = logic.getInstructorsForCourse(courseId);
        } else {
            instructorList.add(logic.getInstructorForEmail(courseId, instructorEmail));
            instructorToShowIndex = Integer.parseInt(index);
        }

        List<String> sectionNames;
        try {
            sectionNames = logic.getSectionNamesForCourse(courseId);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No sections for given course.", HttpStatus.SC_NOT_FOUND);
        }

        List<String> feedbackNames = new ArrayList<>();
        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.getFeedbackSessionName());
        }

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        CourseAttributes courseToEdit = logic.getCourse(courseId);

        CourseEditDetails data = new CourseEditDetails(courseToEdit, instructorList, instructor,
                instructorToShowIndex, sectionNames, feedbackNames);

        return new JsonResult(data);

    }

    /**
     * Output format for {@link GetCourseEditDetailsAction}.
     */
    public static class CourseEditDetails extends ActionResult.ActionOutput {
        CourseAttributes courseToEdit;
        List<InstructorAttributes> instructorList;
        InstructorAttributes instructor;
        int instructorToShowIndex;
        List<String> sectionNames;
        List<String> feedbackNames;

        public CourseEditDetails(CourseAttributes courseToEdit, List<InstructorAttributes> instructorList,
                                 InstructorAttributes instructor, int instructorToShowIndex,
                                 List<String> sectionNames, List<String> feedbackNames) {
            this.courseToEdit = courseToEdit;
            this.instructorList = instructorList;
            this.instructor = instructor;
            this.instructorToShowIndex = instructorToShowIndex;
            this.sectionNames = sectionNames;
            this.feedbackNames = feedbackNames;
        }

        public CourseAttributes getCourseToEdit() {
            return courseToEdit;
        }

        public List<InstructorAttributes> getInstructorList() {
            return instructorList;
        }

        public InstructorAttributes getInstructor() {
            return instructor;
        }

        public int getInstructorToShowIndex() {
            return instructorToShowIndex;
        }

        public List<String> getSectionNames() {
            return sectionNames;
        }

        public List<String> getFeedbackNames() {
            return feedbackNames;
        }
    }

}
