package teammates.ui.template;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.ui.pagedata.AdminAccountDetailsPageData;

public class AdminAccountDetailsInstructorCourseListTableRow {
    private String instructorId;
    private CourseDetailsBundle courseDetails;
    private ElementTag removeFromCourseButton;

    public AdminAccountDetailsInstructorCourseListTableRow(String instructorId, CourseDetailsBundle courseDetails) {
        this.instructorId = instructorId;
        this.courseDetails = courseDetails;
        this.removeFromCourseButton = createRemoveButton();
    }

    public CourseDetailsBundle getCourseDetails() {
        return courseDetails;
    }

    public ElementTag getRemoveFromCourseButton() {
        return this.removeFromCourseButton;
    }

    private ElementTag createRemoveButton() {
        String content = "<span class=\"glyphicon glyphicon-trash\"></span>Remove From Course";
        String href = AdminAccountDetailsPageData.getAdminDeleteInstructorFromCourseLink(instructorId,
                                                                               courseDetails.course.getId());
        return new ElementTag(content, "id", "instructor_" + courseDetails.course.getId(), "class",
                              "btn btn-danger btn-sm", "href", href);
    }
}
