package teammates.ui.template;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class AdminAccountDetailsStudentCourseListTableRow {
    private String googleId;
    private CourseAttributes courseDetails;
    private ElementTag removeFromCourseButton;
    private String sessionToken;

    public AdminAccountDetailsStudentCourseListTableRow(String googleId, CourseAttributes courseDetails,
            String sessionToken) {
        this.googleId = googleId;
        this.courseDetails = courseDetails;
        this.removeFromCourseButton = createRemoveButton();
        this.sessionToken = sessionToken;
    }

    public CourseAttributes getCourseDetails() {
        return courseDetails;
    }

    public ElementTag getRemoveFromCourseButton() {
        return this.removeFromCourseButton;
    }

    private ElementTag createRemoveButton() {
        String content = "<span class=\"glyphicon glyphicon-trash\"></span>Remove From Course";
        String href = getAdminDeleteStudentFromCourseLink(googleId, courseDetails.getId());
        return new ElementTag(content, "id", "student_" + courseDetails.getId(), "class",
                              "btn btn-danger btn-sm", "href", href);
    }

    private String getAdminDeleteStudentFromCourseLink(String studentId, String courseId) {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_ID, studentId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);

        return link;
    }
}
