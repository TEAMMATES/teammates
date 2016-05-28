package teammates.common.util;

public class EmailTemplates {

    public static final String USER_COURSE_JOIN = FileHelper.readResourseFile("userEmailTemplate-courseJoin.html");
    public static final String FRAGMENT_STUDENT_COURSE_JOIN = FileHelper.readResourseFile("studentEmailFragment-courseJoin.html");
    public static final String FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET = FileHelper.readResourseFile("studentEmailFragment-googleIdReset.html");
    public static final String FRAGMENT_INSTRUCTOR_COURSE_JOIN = FileHelper.readResourseFile("instructorEmailFragment-courseJoin.html");
    public static final String USER_FEEDBACK_SESSION = FileHelper.readResourseFile("userEmailTemplate-feedbackSession.html");
    public static final String USER_FEEDBACK_SESSION_CLOSING = FileHelper.readResourseFile("userEmailTemplate-feedbackSessionClosing.html");
    public static final String USER_FEEDBACK_SESSION_PUBLISHED = FileHelper.readResourseFile("userEmailTemplate-feedbackSessionPublished.html");
    public static final String USER_PENDING_COMMENTS_CLEARED = FileHelper.readResourseFile("userEmailTemplate-pendingCommentsCleared.html");
    public static final String SYSTEM_ERROR = FileHelper.readResourseFile("systemErrorEmailTemplate.html");
    public static final String NEW_INSTRCUTOR_ACCOUNT_WELCOME = FileHelper.readResourseFile("newInstructorAccountWelcome.html");
}
