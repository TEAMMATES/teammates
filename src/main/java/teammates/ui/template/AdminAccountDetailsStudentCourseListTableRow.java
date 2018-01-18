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
        this.sessionToken = sessionToken;
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
        String href = getAdminDeleteStudentFromCourseLink();
        return new ElementTag(content, "id", "student_" + courseDetails.getId(), "class",
                              "btn btn-danger btn-sm", "href", href);
    }

    private String getAdminDeleteStudentFromCourseLink() {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_ID, googleId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.getId());
        link = Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);

        return link;
    }
}
