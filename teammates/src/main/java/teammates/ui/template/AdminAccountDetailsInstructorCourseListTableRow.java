package teammates.ui.template;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class AdminAccountDetailsInstructorCourseListTableRow {
    private String instructorId;
    private CourseDetailsBundle courseDetails;
    private ElementTag removeFromCourseButton;
    private String sessionToken;

    public AdminAccountDetailsInstructorCourseListTableRow(String instructorId, CourseDetailsBundle courseDetails,
            String sessionToken) {
        this.instructorId = instructorId;
        this.courseDetails = courseDetails;
        this.sessionToken = sessionToken;
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
        String href = getAdminDeleteInstructorFromCourseLink();
        return new ElementTag(content, "id", "instructor_" + courseDetails.course.getId(), "class",
                              "btn btn-danger btn-sm", "href", href);
    }

    private String getAdminDeleteInstructorFromCourseLink() {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.getId());
        link = Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);

        return link;
    }
}
