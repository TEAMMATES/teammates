package teammates.ui.template;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.ui.pagedata.AdminAccountDetailsPageData;

public class AdminAccountDetailsStudentCourseListTableRow {
    private String googleId;
    private CourseAttributes courseDetails;
    private ElementTag removeFromCourseButton;

    public AdminAccountDetailsStudentCourseListTableRow(String googleId, CourseAttributes courseDetails) {
        this.googleId = googleId;
        this.courseDetails = courseDetails;
        this.removeFromCourseButton = createRemoveButton();
    }

    public CourseAttributes getCourseDetails() {
        return courseDetails;
    }

    public ElementTag getRemoveFromCourseButton() {
        return this.removeFromCourseButton;
    }

    private ElementTag createRemoveButton() {
        String content = "<span class=\"glyphicon glyphicon-trash\"></span>Remove From Course";
        String href = AdminAccountDetailsPageData.getAdminDeleteStudentFromCourseLink(googleId, courseDetails.getId());
        return new ElementTag(content, "id", "student_" + courseDetails.getId(), "class",
                              "btn btn-danger btn-sm", "href", href);
    }
}
