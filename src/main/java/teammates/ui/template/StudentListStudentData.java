package teammates.ui.template;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Url;

public class StudentListStudentData {

    private String studentName;
    private String studentEmail;
    private String studentStatus;
    private String studentNameForJs;
    private String courseIdForJs;
    private String photoUrl;
    private String courseStudentDetailsLink;
    private String courseStudentEditLink;
    private String courseStudentRemindLink;
    private String courseStudentDeleteLink;
    private String courseStudentRecordsLink;
    private String sessionToken;

    public StudentListStudentData(String googleId, String studentName, String studentEmail, String course,
                                  String studentStatus, String photoUrl, String sessionToken, String previousPage) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentStatus = studentStatus;
        this.studentNameForJs = SanitizationHelper.sanitizeForJs(studentName);
        this.courseIdForJs = SanitizationHelper.sanitizeForJs(course);
        this.photoUrl = photoUrl;
        this.sessionToken = sessionToken;
        this.courseStudentDetailsLink =
                furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE,
                                                    course, studentEmail, googleId, null);
        this.courseStudentEditLink =
                furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT,
                                                    course, studentEmail, googleId, null);
        this.courseStudentRemindLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_REMIND,
                                                                           course, studentEmail, googleId, previousPage);
        this.courseStudentDeleteLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE,
                                                                           course, studentEmail, googleId, null);
        this.courseStudentRecordsLink = furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE,
                                                                            course, studentEmail, googleId, null);
    }

    private String furnishLinkWithCourseEmailAndUserId(String rawLink, String course, String studentEmail,
                                                       String googleId, String previousPage) {
        String link = rawLink;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        link = Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_REMIND_STUDENT_IS_FROM, previousPage);
        return link;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    public String getStudentNameForJs() {
        return studentNameForJs;
    }

    public String getCourseIdForJs() {
        return courseIdForJs;
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

    public String getCourseStudentRemindLink() {
        return courseStudentRemindLink;
    }

    public String getCourseStudentDeleteLink() {
        return courseStudentDeleteLink;
    }

    public String getCourseStudentRecordsLink() {
        return courseStudentRecordsLink;
    }

}
