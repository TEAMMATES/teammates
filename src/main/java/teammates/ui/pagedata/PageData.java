package teammates.ui.pagedata;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackResponseCommentRow;

/**
 * Data and utility methods needed to render a specific page.
 */
public class PageData {

    /** The user for whom the pages are displayed (i.e. the 'nominal user').
     *  May not be the logged in user (under masquerade mode) */
    public AccountAttributes account;
    public StudentAttributes student;

    private List<StatusMessage> statusMessagesToUser;

    private String sessionToken;

    public PageData(AccountAttributes account, String sessionToken) {
        this(account, null, sessionToken);
    }

    public PageData(AccountAttributes account, StudentAttributes student, String sessionToken) {
        this.account = account;
        this.student = student;
        this.sessionToken = sessionToken;
    }

    public AccountAttributes getAccount() {
        return account;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public boolean isUnregisteredStudent() {
        return account.googleId == null || student != null && !student.isRegistered();
    }

    /* These util methods simply delegate the work to the matching *Helper
     * class. We keep them here so that JSP pages do not have to import
     * those *Helper classes.
     */
    public static String sanitizeForHtml(String unsanitizedStringLiteral) {
        return SanitizationHelper.sanitizeForHtml(unsanitizedStringLiteral);
    }

    public String addUserIdToUrl(String link) {
        return Url.addParamToUrl(link, Const.ParamsNames.USER_ID, account.googleId);
    }

    public String addSessionTokenToUrl(String link) {
        return Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);
    }

    /**
     * Returns an element tag representing a HTML option.
     */
    public static ElementTag createOption(String text, String value, boolean isSelected) {
        if (isSelected) {
            return new ElementTag(text, "value", value, "selected", null);
        }
        return new ElementTag(text, "value", value);
    }

    public String getStudentProfilePictureLink(String studentEmail, String courseId) {
        String link = Const.ActionURIs.STUDENT_PROFILE_PICTURE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseEnrollLink(String courseId) {
        String link = Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackEditLink(String courseId, String feedbackSessionName, boolean shouldLoadInEditMode) {
        String link = Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT,
                Boolean.toString(shouldLoadInEditMode));
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackResultsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackPublishLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorFeedbackUnpublishLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorSearchLink() {
        String link = Const.WebPageURIs.INSTRUCTOR_SEARCH_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes qn,
                                              FeedbackParticipantType viewerType) {
        if (viewerType == FeedbackParticipantType.GIVER) {
            return true;
        }
        return qn.isResponseVisibleTo(viewerType);
    }

    public boolean isResponseCommentGiverNameVisibleTo(FeedbackQuestionAttributes qn,
                                                       FeedbackParticipantType viewerType) {
        return true;
    }

    public String getResponseCommentVisibilityString(FeedbackQuestionAttributes qn) {
        String visibilityString = StringHelper.removeEnclosingSquareBrackets(qn.showResponsesTo.toString());
        return StringHelper.isWhiteSpace(visibilityString) ? "GIVER" : "GIVER, " + visibilityString;
    }

    public String getResponseCommentVisibilityString(FeedbackResponseCommentAttributes frComment,
                                                     FeedbackQuestionAttributes qn) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return getResponseCommentVisibilityString(qn);
        }
        return StringHelper.removeEnclosingSquareBrackets(frComment.showCommentTo.toString());
    }

    public String getResponseCommentGiverNameVisibilityString(FeedbackQuestionAttributes qn) {
        return getResponseCommentVisibilityString(qn);
    }

    public String getResponseCommentGiverNameVisibilityString(FeedbackResponseCommentAttributes frComment,
                                                              FeedbackQuestionAttributes qn) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return getResponseCommentGiverNameVisibilityString(qn);
        }
        return StringHelper.removeEnclosingSquareBrackets(frComment.showGiverNameTo.toString());
    }

    /**
     * Sets the list of status messages.
     * @param statusMessagesToUser a list of status messages that is to be displayed to the user
     */
    public void setStatusMessagesToUser(List<StatusMessage> statusMessagesToUser) {
        this.statusMessagesToUser = statusMessagesToUser;
    }

    /**
     * Gets the list of status messages.
     * @return a list of status messages that is to be displayed to the user
     */
    public List<StatusMessage> getStatusMessagesToUser() {
        return statusMessagesToUser;
    }

    /**
     * Builds template that will be used by feedbackParticipant/Instructor to add comments to responses.
     *
     * @param question question of response
     * @param responseId id of response (can be empty)
     * @param giverName name of person/team giving comment (empty for feedback participant comments)
     * @param recipientName name of person/team receiving comment (empty for feedback participant comments)
     * @param timezone Time zone
     * @param isCommentFromFeedbackParticipant true if comment giver is feedback participant
     * @return Feedback response comment add form template
     */
    public FeedbackResponseCommentRow buildFeedbackResponseCommentFormForAdding(FeedbackQuestionAttributes question,
            String responseId, String giverName, String recipientName, ZoneId timezone,
            boolean isCommentFromFeedbackParticipant) {
        FeedbackResponseCommentAttributes frca = FeedbackResponseCommentAttributes.builder()
                .withCourseId(question.courseId)
                .withFeedbackSessionName(question.feedbackSessionName)
                .withCommentGiver("")
                .withCommentText("")
                .withFeedbackResponseId(responseId)
                .withFeedbackQuestionId(question.getFeedbackQuestionId())
                .withCommentFromFeedbackParticipant(isCommentFromFeedbackParticipant)
                .build();

        frca.showCommentTo = new ArrayList<>();
        frca.showGiverNameTo = new ArrayList<>();
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS,
        };

        for (FeedbackParticipantType type : relevantTypes) {
            if (isResponseCommentVisibleTo(question, type)) {
                frca.showCommentTo.add(type);
            }
            if (isResponseCommentGiverNameVisibleTo(question, type)) {
                frca.showGiverNameTo.add(type);
            }
        }

        return new FeedbackResponseCommentRow(frca, giverName, recipientName,
                getResponseCommentVisibilityString(question),
                getResponseCommentGiverNameVisibilityString(question), getResponseVisibilityMap(question),
                timezone);
    }

    /**
     * Returns map in which key is feedback participant and value determines whether response is visible to it.
     *
     * @param question question associated with response
     * @return map of all feedback participants as keys
     */
    public Map<FeedbackParticipantType, Boolean> getResponseVisibilityMap(FeedbackQuestionAttributes question) {
        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = new HashMap<>();

        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS,
        };

        for (FeedbackParticipantType participantType : relevantTypes) {
            responseVisibilityMap.put(participantType, isResponseVisibleTo(participantType, question));
        }

        return responseVisibilityMap;
    }

    // TODO investigate and fix the differences between question.isResponseVisibleTo and this method
    protected boolean isResponseVisibleTo(FeedbackParticipantType participantType, FeedbackQuestionAttributes question) {
        switch (participantType) {
        case GIVER:
            return question.isResponseVisibleTo(FeedbackParticipantType.GIVER);
        case INSTRUCTORS:
            return question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
        case OWN_TEAM_MEMBERS:
            return question.giverType != FeedbackParticipantType.INSTRUCTORS
                    && question.giverType != FeedbackParticipantType.SELF
                    && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        case RECEIVER:
            return question.recipientType != FeedbackParticipantType.SELF
                    && question.recipientType != FeedbackParticipantType.NONE
                    && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
        case RECEIVER_TEAM_MEMBERS:
            return question.recipientType != FeedbackParticipantType.INSTRUCTORS
                    && question.recipientType != FeedbackParticipantType.SELF
                    && question.recipientType != FeedbackParticipantType.NONE
                    && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        case STUDENTS:
            return question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
        default:
            Assumption.fail("Invalid participant type");
            return false;
        }
    }

    /**
     * Builds comment row for feedback participant comment.
     *
     * @param questionAttributes question associated with comment
     * @param commentsForResponses map where key is response id and value is list of comments on that response
     * @param responseId response id of response associated with comment
     * @param isEditDeleteEnabled true if comment can be edited or deleted
     * @return
     */
    public FeedbackResponseCommentRow buildFeedbackParticipantResponseCommentRow(
            FeedbackQuestionAttributes questionAttributes,
            Map<String, List<FeedbackResponseCommentAttributes>> commentsForResponses, String responseId,
            boolean isEditDeleteEnabled) {
        if (!commentsForResponses.containsKey(responseId)) {
            return null;
        }
        List<FeedbackResponseCommentAttributes> frcList = commentsForResponses.get(responseId);
        for (FeedbackResponseCommentAttributes frcAttributes : frcList) {
            if (frcAttributes.isCommentFromFeedbackParticipant) {
                return new FeedbackResponseCommentRow(frcAttributes, questionAttributes, isEditDeleteEnabled);
            }
        }
        return null;
    }
}
