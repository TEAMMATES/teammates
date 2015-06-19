package teammates.ui.template;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Url;

public class InstructorStudentListAjaxStudentData {

    private String googleId;
    private String studentName;
    private String studentEmail;
    private String toggleDeleteConfirmationParams;
    private String photoUrl;
    private String courseStudentDetailsLink;
    private String courseStudentEditLink;
    private String courseStudentDeleteLink;
    private String courseStudentRecordsLink;

    public InstructorStudentListAjaxStudentData(String googleId, String studentName, String studentEmail,
                                                String course, String photoUrl) {
        this.googleId = googleId;
        this.studentName = Sanitizer.sanitizeForHtml(studentName);
        this.studentEmail = Sanitizer.sanitizeForHtml(studentEmail);
        this.toggleDeleteConfirmationParams = "'" + Sanitizer.sanitizeForJs(course) + "','"
                                            + Sanitizer.sanitizeForJs(studentName) + "'";
        this.photoUrl = photoUrl;
        this.courseStudentDetailsLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE,
                                                                            course, studentEmail, googleId);
        this.courseStudentEditLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT,
                                                                         course, studentEmail, googleId);
        this.courseStudentDeleteLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE,
                                                                           course, studentEmail, googleId);
        this.courseStudentRecordsLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE,
                                                                            course, studentEmail, googleId);
    }

    private String furnishLinkWithCourseEmailAndUserId(String link, String course, String studentEmail,
                                                       String googleId) {
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link.replace("%40", "@");
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getToggleDeleteConfirmationParams() {
        return toggleDeleteConfirmationParams;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getCourseStudentDetailsLink() {
        return courseStudentDetailsLink;
    }

    public String getCourseStudentEditLink() {
        return courseStudentEditLink;
    }

    public String getCourseStudentDeleteLink() {
        return courseStudentDeleteLink;
    }

    public String getCourseStudentRecordsLink() {
        return courseStudentRecordsLink;
    }

}
