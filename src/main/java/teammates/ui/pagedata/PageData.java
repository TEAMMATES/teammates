package teammates.ui.pagedata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.NationalityHelper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.InstructorFeedbackSessionActions;

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

    public static String sanitizeForJs(String unsanitizedStringLiteral) {
        return SanitizationHelper.sanitizeForJs(unsanitizedStringLiteral);
    }

    public static String truncate(String untruncatedString, int truncateLength) {
        return StringHelper.truncate(untruncatedString, truncateLength);
    }

    public String addUserIdToUrl(String link) {
        return Url.addParamToUrl(link, Const.ParamsNames.USER_ID, account.googleId);
    }

    public String addSessionTokenToUrl(String link) {
        return Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);
    }

    /**
     * Returns the timezone options as HTML code.
     * None is selected, since the selection should only be done in client side.
     */
    protected List<String> getTimeZoneOptionsAsHtml(ZoneId existingTimeZone) {
        List<ZoneId> options = TimeHelper.getTimeZoneValues();
        ArrayList<String> result = new ArrayList<>();
        for (ZoneId timeZoneOption : options) {
            result.add("<option value=\"" + timeZoneOption.getId() + "\""
                    + (existingTimeZone.equals(timeZoneOption) ? " selected" : "") + ">" + "(" + timeZoneOption.getId()
                    + ") " + TimeHelper.getCitiesForTimeZone(timeZoneOption) + "</option>");
        }
        return result;
    }

    /**
     * Returns the nationalities as HTML code.
     */
    public static List<ElementTag> getNationalitiesAsElementTags(String existingNationality) {
        List<String> nationalities = NationalityHelper.getNationalities();
        List<ElementTag> result = new ArrayList<>();

        result.add(createOption("--- Select ---", "", !nationalities.contains(existingNationality)));

        for (String nationality : nationalities) {
            ElementTag option = createOption(nationality, nationality, nationality.equals(existingNationality));
            result.add(option);
        }

        return result;
    }

    /**
     * Creates and returns a String if the existing nationality is incorrect.
     */
    public static String getLegacyNationalityInstructions(String existingNationality) {
        List<String> nationalities = NationalityHelper.getNationalities();

        if (nationalities.contains(existingNationality) || "".equals(existingNationality)) {
            return "";
        }
        return "Previously entered value was " + SanitizationHelper.sanitizeForHtml(existingNationality) + ". "
               + "This is not a valid nationality; "
               + "please choose a valid nationality from the dropdown list before saving.";
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

    /**
     * Returns an element tag representing a HTML option.
     */
    public static ElementTag createOption(String text, String value) {
        return new ElementTag(text, "value", value);
    }

    /**
     * Returns the grace period options as HTML code.
     */
    public static List<ElementTag> getGracePeriodOptionsAsElementTags(long existingGracePeriod) {
        ArrayList<ElementTag> result = new ArrayList<>();
        for (int i = 0; i <= 30; i += 5) {
            ElementTag option = createOption(i + " mins", String.valueOf(i),
                                            isGracePeriodToBeSelected(existingGracePeriod, i));
            result.add(option);
        }
        return result;
    }

    /**
     * Returns the time options as HTML code.
     * By default the selected one is the last one.
     */
    public static List<ElementTag> getTimeOptionsAsElementTags(LocalDateTime timeToShowAsSelected) {
        List<ElementTag> result = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            ElementTag option = createOption(String.format("%04dH", i * 100 - (i == 24 ? 41 : 0)),
                                             String.valueOf(i), isTimeToBeSelected(timeToShowAsSelected, i));
            result.add(option);
        }
        return result;
    }

    //TODO: methods below this point should be made 'protected' and only the
    //  child classes that need them should expose them using public methods
    //  with similar name. That way, we know which child needs which method.

    /**
     * Returns the status of the student, whether he has joined the course.
     * This is based on googleId, if it's null or empty, then we assume he
     * has not joined the course yet.
     * @return "Yet to Join" or "Joined"
     */
    public String getStudentStatus(StudentAttributes student) {
        return student.getStudentStatus();
    }

    /**
     * Returns The relative path to the student home page. Defaults to whether the student is unregistered.
     */
    public String getStudentHomeLink() {
        return getStudentHomeLink(isUnregisteredStudent());
    }

    /**
     * Returns The relative path to the student home page. The user Id is encoded in the url as a parameter.
     */
    public String getStudentHomeLink(boolean isUnregistered) {
        String link = Const.ActionURIs.STUDENT_HOME_PAGE;
        link = addUserIdToUrl(link);
        if (isUnregistered) {
            link = Url.addParamToUrl(student.getRegistrationUrl(), Const.ParamsNames.NEXT_URL, link);
        }
        return link;
    }

    /**
     * Returns The relative path to the student profile page. Defaults to whether the student is unregistered.
     */
    public String getStudentProfileLink() {
        return getStudentProfileLink(isUnregisteredStudent());
    }

    /**
     * Returns The relative path to the student profile page. The user Id is encoded in the url as a parameter.
     */
    public String getStudentProfileLink(boolean isUnregistered) {
        String link = Const.ActionURIs.STUDENT_PROFILE_PAGE;
        link = addUserIdToUrl(link);
        if (isUnregistered) {
            link = Url.addParamToUrl(student.getRegistrationUrl(), Const.ParamsNames.NEXT_URL, link);
        }
        return link;
    }

    public String getStudentCourseDetailsLink(String courseId) {
        String link = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
        link = addUserIdToUrl(link);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        return link;
    }

    public String getStudentFeedbackSubmissionEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getStudentFeedbackResultsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getStudentProfilePictureLink(String studentEmail, String courseId) {
        String link = Const.ActionURIs.STUDENT_PROFILE_PICTURE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    /**
     * Returns The relative path to the instructor home page. The user Id is encoded in the url as a parameter.
     */
    public String getInstructorHomeLink() {
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCoursesLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseEnrollLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseEnrollSaveLink(String courseId) {
        //TODO: instead of using this method, the form should include these data as hidden fields?
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseDetailsLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseEditLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackStatsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseStatsLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackEditCopyLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorQuestionCopyPageLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_PAGE;
        return addUserIdToUrl(link);
    }

    /**
     * Retrieves the link to submit the request for copy of session.
     * Appends the return url to the link.
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackEditCopyActionLink(String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorCourseDeleteLink(String courseId, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,
                                 Const.ParamsNames.NEXT_URL,
                                 isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                                        : Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseArchiveLink(String courseId, boolean archiveStatus, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ARCHIVE_STATUS, Boolean.toString(archiveStatus));
        link = Url.addParamToUrl(link,
                                 Const.ParamsNames.NEXT_URL,
                                 isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                                        : Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseRestoreSoftDeletedCourseLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_RESTORE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorCourseRestoreAllSoftDeletedCoursesLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_RESTORE_ALL;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorCourseDeleteSoftDeletedCourseLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorCourseDeleteAllSoftDeletedCoursesLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_DELETE_ALL;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorFeedbackSessionsLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackSessionsLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE;
        link = addUserIdToUrl(link);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        return link;
    }

    /**
     * Retrieves the link to submit request to delete the session.
     * @param courseId course ID
     * @param feedbackSessionName the session name
     * @param returnUrl the url of the page to return to after the delete
     * @return the link to submit request to delete the session with return page link
     */
    public String getInstructorFeedbackDeleteLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorFeedbackEditLink(String courseId, String feedbackSessionName, boolean shouldLoadInEditMode) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT,
                Boolean.toString(shouldLoadInEditMode));
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorFeedbackSubmissionEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
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

    /**
     * Retrieves the link to submit the request for remind student
     * Appends the return url to the link.
     * @param courseId the course ID
     * @param feedbackSessionName the name of the feedback session
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackRemindLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    /**
     * Retrieves the link to load remind modal.
     * @param courseId the course ID
     * @param feedbackSessionName the name of the feedback session
     * @return the link to load remind modal
     */
    public String getInstructorFeedbackRemindParticularStudentsPageLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    /**
     * Retrieves the link to submit the request to remind a particular student(s).
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackRemindParticularStudentsLink(String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addSessionTokenToUrl(link);

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

    /**
     * Retrieves the link to load data for the resend session published email modal.
     * @param courseId the course ID
     * @param feedbackSessionName the name of the feedback session
     * @return the link to load data for the modal
     */
    public String getInstructorFeedbackResendPublishedEmailPageLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESEND_PUBLISHED_EMAIL_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    /**
     * Retrieves the link to submit the request for resending the session published email.
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackResendPublishedEmailLink(String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESEND_PUBLISHED_EMAIL;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
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

    public String getInstructorFeedbackRestoreSoftDeletedSessionLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_RESTORE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorFeedbackRestoreAllSoftDeletedSessionsLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_RESTORE_ALL;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorFeedbackDeleteSoftDeletedSessionLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorFeedbackDeleteAllSoftDeletedSessionsLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_DELETE_ALL;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);

        return link;
    }

    public String getInstructorStudentListLink() {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorSearchLink() {
        String link = Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorStudentRecordsLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseRemindLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseStudentDetailsLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseStudentDetailsEditLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseRemindStudentLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    // TODO: create another delete action which redirects to studentListPage?
    public String getInstructorCourseStudentDeleteLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseStudentDeleteAllLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE_ALL;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseInstructorDeleteLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public String getInstructorCourseRemindInstructorLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        link = addSessionTokenToUrl(link);
        return link;
    }

    public static String getInstructorSubmissionStatusForFeedbackSession(FeedbackSessionAttributes session) {
        if (session.isOpened()) {
            return "Open";
        } else if (session.isWaitingToOpen()) {
            return "Awaiting";
        } else {
            return "Closed";
        }
    }

    public static String getInstructorPublishedStatusForFeedbackSession(FeedbackSessionAttributes session) {
        if (session.isPublished()) {
            return "Published";
        } else {
            return "Not Published";
        }
    }

    public static String getInstructorSubmissionsTooltipForFeedbackSession(FeedbackSessionAttributes session) {

        StringBuilder msg = new StringBuilder(50);
        msg.append("The feedback session has been created");

        if (session.isVisible()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_VISIBLE);
        }

        if (session.isOpened()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_OPEN);
        } else if (session.isWaitingToOpen()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_AWAITING);
        } else if (session.isClosed()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_CLOSED);
        }

        msg.append('.');

        return msg.toString();
    }

    public static String getInstructorPublishedTooltipForFeedbackSession(FeedbackSessionAttributes session) {
        if (session.isPublished()) {
            return Const.Tooltips.FEEDBACK_SESSION_STATUS_PUBLISHED;
        } else {
            return Const.Tooltips.FEEDBACK_SESSION_STATUS_NOT_PUBLISHED;
        }
    }

    /**
     * Returns the links of actions available for a specific session.
     *
     * @param session
     *         The feedback session details
     * @param returnUrl
     *         The return URL after performing the action.
     * @param instructor
     *         The Instructor details
     */
    public InstructorFeedbackSessionActions getInstructorFeedbackSessionActions(FeedbackSessionAttributes session,
                                                                                String returnUrl,
                                                                                InstructorAttributes instructor) {
        return new InstructorFeedbackSessionActions(this, session, returnUrl, instructor);
    }

    private static boolean isTimeToBeSelected(LocalDateTime timeToShowAsSelected, int hourOfTheOption) {
        boolean isEditingExistingFeedbackSession = timeToShowAsSelected != null;
        if (isEditingExistingFeedbackSession) {
            if (timeToShowAsSelected.getMinute() == 0) {
                if (timeToShowAsSelected.getHour() == hourOfTheOption) {
                    return true;
                }
            } else {
                if (hourOfTheOption == 24) {
                    return true;
                }
            }
        } else {
            if (hourOfTheOption == 24) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGracePeriodToBeSelected(long existingGracePeriodValue, long gracePeriodOptionValue) {
        boolean isEditingExistingEvaluation = existingGracePeriodValue != Const.INT_UNINITIALIZED;
        if (isEditingExistingEvaluation) {
            return gracePeriodOptionValue == existingGracePeriodValue;
        }
        int defaultGracePeriod = 15;
        return gracePeriodOptionValue == defaultGracePeriod;
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

    public String getPictureUrl(String pictureKey) {
        if (pictureKey == null || pictureKey.isEmpty()) {
            return Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        }
        return Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?"
               + Const.ParamsNames.BLOB_KEY + "=" + pictureKey + "&"
               + Const.ParamsNames.USER_ID + "=" + account.googleId;
    }

    public String getRecipientNames(Set<String> recipients, String courseId, String studentEmail, CourseRoster roster) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;

        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append(" and ");
            } else if (i > 0 && i < recipients.size() - 1 && recipients.size() > 2) {
                namesStringBuilder.append(", ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (recipient.equals(studentEmail)) {
                namesStringBuilder.append("you");
            } else if (courseId.equals(recipient)) {
                namesStringBuilder.append("all students in this course");
            } else if (student == null) {
                namesStringBuilder.append(recipient);
            } else {
                if (recipients.size() == 1) {
                    namesStringBuilder.append(student.name + " (" + student.team + ", " + student.email + ")");
                } else {
                    namesStringBuilder.append(student.name);
                }
            }
            i++;
        }
        return namesStringBuilder.toString();
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
        FeedbackResponseCommentAttributes frca = FeedbackResponseCommentAttributes
                .builder(question.courseId, question.feedbackSessionName, "", new Text(""))
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
                FeedbackParticipantType.INSTRUCTORS
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
                FeedbackParticipantType.INSTRUCTORS
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
