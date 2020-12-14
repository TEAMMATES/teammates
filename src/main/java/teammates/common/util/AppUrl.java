package teammates.common.util;

/**
 * A specific implementation of {@link Url} used to encapsulate URLs of the application.
 */
public class AppUrl extends Url {

    public AppUrl(String url) {
        super(url);
        Assumption.assertTrue(url.startsWith("http")); // must either be http or https
    }

    public AppUrl withUserId(String userId) {
        return withParam(Const.ParamsNames.USER_ID, userId);
    }

    public AppUrl withRegistrationKey(String key) {
        return withParam(Const.ParamsNames.REGKEY, key);
    }

    public AppUrl withInstructorInstitution(String institute) {
        return withParam(Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
    }

    public AppUrl withInstitutionMac(String mac) {
        return withParam(Const.ParamsNames.INSTITUTION_MAC, mac);
    }

    public AppUrl withCourseId(String courseId) {
        return withParam(Const.ParamsNames.COURSE_ID, courseId);
    }

    public AppUrl withSessionName(String feedbackSessionName) {
        return withParam(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
    }

    public AppUrl withStudentEmail(String email) {
        return withParam(Const.ParamsNames.STUDENT_EMAIL, email);
    }

    public AppUrl withEntityType(String entityType) {
        return withParam(Const.ParamsNames.ENTITY_TYPE, entityType);
    }

}
