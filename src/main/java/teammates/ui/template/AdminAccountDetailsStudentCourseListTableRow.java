package teammates.ui.template;

import teammates.common.datatransfer.CourseAttributes;
import teammates.ui.controller.AdminAccountDetailsPageData;

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
        String href = AdminAccountDetailsPageData.getAdminDeleteStudentFromCourseLink(googleId, courseDetails.id);
        return new ElementTag(content, "id", "student_" + courseDetails.id, "class",
                              "btn btn-danger btn-sm", "href", href);
    }
}
