package teammates.common.util;

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
        Assumption.assertTrue("The number of elements in keyValuePairs passed in must be even",
                keyValuePairs.length % 2 == 0);
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
        public static final String USER_COURSE_JOIN =
                FileHelper.readResourceFile("userEmailTemplate-courseJoin.html");
        public static final String USER_COURSE_REGISTER =
                FileHelper.readResourceFile("userEmailTemplate-userRegisterForCourse.html");
        public static final String FRAGMENT_STUDENT_COURSE_JOIN =
                FileHelper.readResourceFile("studentEmailFragment-courseJoin.html");
        public static final String FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET =
                FileHelper.readResourceFile("studentEmailFragment-googleIdReset.html");
        public static final String FRAGMENT_INSTRUCTOR_COURSE_JOIN =
                FileHelper.readResourceFile("instructorEmailFragment-courseJoin.html");
        public static final String FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET =
                FileHelper.readResourceFile("instructorEmailFragment-googleIdReset.html");
        public static final String USER_FEEDBACK_SESSION =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSession.html");
        public static final String USER_FEEDBACK_SESSION_PUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionPublished.html");
        public static final String FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_SESSION =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplateFragment-sessionAccessLinksBySession.html");
        public static final String FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_COURSE =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplateFragment-sessionAccessLinksByCourse.html");
        public static final String SESSION_LINKS_RECOVERY_ACCESS_LINKS =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplate-feedbackSessionAccessLinks.html");
        public static final String SESSION_LINKS_RECOVERY_ACCESS_LINKS_NONE =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplate-feedbackSessionAccessLinksNone.html");
        public static final String SESSION_LINKS_RECOVERY_EMAIL_NOT_FOUND =
                FileHelper.readResourceFile("sessionLinksRecoveryEmailTemplate-emailNotFound.html");
        public static final String USER_FEEDBACK_SESSION_UNPUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionUnpublished.html");
        public static final String FRAGMENT_SINGLE_FEEDBACK_SESSION_LINKS =
                FileHelper.readResourceFile("userEmailTemplateFragment-feedbackSessionResendAllLinks.html");
        public static final String USER_FEEDBACK_SESSION_RESEND_ALL_LINKS =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionResendAllLinks.html");
        public static final String USER_REGKEY_REGENERATION_RESEND_ALL_COURSE_LINKS =
                FileHelper.readResourceFile("userEmailTemplate-regenerateLinksResendAllCourseLinks.html");
        public static final String SEVERE_ERROR_LOG_LINE =
                FileHelper.readResourceFile("severeErrorLogLine.html");
        public static final String NEW_INSTRUCTOR_ACCOUNT_WELCOME =
                FileHelper.readResourceFile("newInstructorAccountWelcome.html");
        public static final String FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION =
                FileHelper.readResourceFile("userEmailFragment-sessionAdditionalContactInformationFragment.html");
    }

}
