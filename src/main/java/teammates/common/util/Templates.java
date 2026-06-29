package teammates.common.util;

/**
 * Contains utility methods for creating strings from given templates.
 */
public final class Templates {

    public static final String INSTRUCTOR_SAMPLE_DATA = FileHelper.readResourceFile("InstructorSampleData.json");

    private Templates() {
        // utility class
    }

    /**
     * Populates the HTML templates by replacing variables in the template string
     * with the given value strings.
     * @param template The template html to be populated
     * @param keyValuePairs Array of a variable, even number of key-value pairs:
     *                   { "key1", "val1", "key2", "val2", ... }
     * @return The populated template
     */
    public static String populateTemplate(String template, String... keyValuePairs) {
        assert keyValuePairs.length % 2 == 0 : "The number of elements in keyValuePairs passed in must be even";
        String populatedTemplate = template;
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            populatedTemplate = populatedTemplate.replace(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return populatedTemplate;
    }

    /**
     * Collection of templates of emails to be sent by the system.
     */
    public static class EmailTemplates {
        public static final String ADMIN_NEW_ACCOUNT_VERIFICATION_REQUEST_ALERT =
                FileHelper.readResourceFile("adminEmailTemplate-newAccountVerificationRequestAlert.html");
        public static final String INSTRUCTOR_NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT =
                FileHelper.readResourceFile("instructorEmailTemplate-newAccountVerificationRequestAcknowledgement.html");
        public static final String USER_COURSE_REGISTER =
                FileHelper.readResourceFile("userEmailTemplate-userRegisterForCourse.html");
        public static final String STUDENT_COURSE_JOIN =
                FileHelper.readResourceFile("studentEmailTemplate-courseJoin.html");
        public static final String STUDENT_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT =
                FileHelper.readResourceFile("studentEmailTemplate-courseRejoinAfterUnlinkAccount.html");
        public static final String INSTRUCTOR_COURSE_JOIN =
                FileHelper.readResourceFile("instructorEmailTemplate-courseJoin.html");
        public static final String INSTRUCTOR_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT =
                FileHelper.readResourceFile("instructorEmailTemplate-courseRejoinAfterUnlinkAccount.html");
        public static final String FRAGMENT_STUDENT_COURSE_JOIN =
                FileHelper.readResourceFile("studentEmailFragment-courseJoin.html");
        public static final String FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_KEY_RESET =
                FileHelper.readResourceFile("studentEmailFragment-keyReset.html");
        public static final String FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_KEY_RESET =
                FileHelper.readResourceFile("instructorEmailFragment-keyReset.html");
        public static final String USER_FEEDBACK_SESSION_OPENED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionOpening.html");
        public static final String USER_FEEDBACK_SESSION_OPENED_PREVIEW =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionOpeningPreview.html");
        public static final String USER_FEEDBACK_SESSION_REMINDER =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionReminder.html");
        public static final String USER_FEEDBACK_SESSION_REMINDER_PREVIEW =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionReminderPreview.html");
        public static final String USER_FEEDBACK_SESSION_CLOSING_SOON =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionClosingSoon.html");
        public static final String USER_FEEDBACK_SESSION_CLOSING_SOON_PREVIEW =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionClosingSoonPreview.html");
        public static final String USER_FEEDBACK_SESSION_PUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionPublished.html");
        public static final String USER_FEEDBACK_SESSION_PUBLISHED_PREVIEW =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionPublishedPreview.html");
        public static final String USER_FEEDBACK_SESSION_UNPUBLISHED_PREVIEW =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionUnpublishedPreview.html");
        public static final String FRAGMENT_COURSE_SESSION_LINKS_BY_SESSION =
                FileHelper.readResourceFile("emailTemplateFragment-courseSessionLinksBySession.html");
        public static final String FRAGMENT_COURSE_SESSION_LINKS_BY_COURSE =
                FileHelper.readResourceFile("emailTemplateFragment-courseSessionLinksByCourse.html");
        public static final String SESSION_LINKS_RECOVERY_EMAIL_NOT_FOUND =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplate-emailNotFound.html");
        public static final String SESSION_LINKS_RECOVERY_EMAIL_FOUND =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplate-found.html");
        public static final String USER_FEEDBACK_SESSION_UNPUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionUnpublished.html");
        public static final String USER_FEEDBACK_SESSION_RESEND_ALL_LINKS =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionResendAllLinks.html");
        public static final String USER_KEY_REGENERATION_RESEND_ALL_COURSE_LINKS =
                FileHelper.readResourceFile("userEmailTemplate-regenerateLinksResendAllCourseLinks.html");
        public static final String ACCOUNT_VERIFICATION_APPROVED =
                FileHelper.readResourceFile("accountVerificationApproved.html");
        public static final String ACCOUNT_VERIFICATION_REJECTED =
                FileHelper.readResourceFile("accountVerificationRejected.html");
        public static final String FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION =
                FileHelper.readResourceFile("userEmailFragment-sessionAdditionalContactInformationFragment.html");
        public static final String USER_DEADLINE_EXTENSION =
                FileHelper.readResourceFile("userEmailTemplate-deadlineExtension.html");
        public static final String OWNER_FEEDBACK_SESSION_OPENING_SOON =
                FileHelper.readResourceFile("ownerEmailTemplate-feedbackSessionOpeningSoon.html");
        public static final String OWNER_FEEDBACK_SESSION_OPENING_SOON_NOT_JOINED =
                FileHelper.readResourceFile("ownerEmailTemplate-feedbackSessionOpeningSoonNotJoined.html");
        public static final String OWNER_FEEDBACK_SESSION_CLOSED =
                FileHelper.readResourceFile("ownerEmailTemplate-feedbackSessionClosed.html");
    }

}
