package teammates.common.util;

/**
 * A specific implementation of {@link Url} used to encapsulate URLs of the application.
 */
public class AppUrl extends Url {

    public AppUrl(String url) {
        super(url);
        assert url.startsWith("http"); // must either be http or https
    }

    public AppUrl withUser(String userId) {
        return withParam(Const.ParamsNames.USER, userId);
    }

    public AppUrl withRegistrationKey(String key) {
        return withParam(Const.ParamsNames.REGKEY, key);
    }

    public AppUrl withIsCreatingAccount(String isCreatingAccount) {
        return withParam(Const.ParamsNames.IS_CREATING_ACCOUNT, isCreatingAccount);
    }

    public AppUrl withCourseId(String courseId) {
        return withParam(Const.ParamsNames.COURSE_ID, courseId);
    }

    public AppUrl withFeedbackSessionId(String feedbackSessionId) {
        return withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId);
    }

    public AppUrl withUserId(String userId) {
        return withParam(Const.ParamsNames.USER_ID, userId);
    }

    public AppUrl withEntityType(String entityType) {
        return withParam(Const.ParamsNames.ENTITY_TYPE, entityType);
    }

    public AppUrl withPreviewAs(String previewAs) {
        return withParam(Const.ParamsNames.PREVIEWAS, previewAs);
    }

}
