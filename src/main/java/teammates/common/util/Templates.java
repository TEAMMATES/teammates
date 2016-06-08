package teammates.common.util;

public final class Templates {
    
    public static final String INSTRUCTOR_SAMPLE_DATA = FileHelper.readResourceFile("InstructorSampleData.json");
    
    private Templates() {
        // utility class
    }
    
    /**
     * Populates the HTML templates by replacing variables in the template string
     * with the given value string.
     * @param template The template html to be populated
     * @param values Array of a variable, even number of key-value pairs:
     *                   { "key1", "val1", "key2", "val2", ... }
     * @return The populated template
     */
    public static String populateTemplate(String template, String... values) {
        Assumption.assertTrue("The number of values passed in must be even", values.length % 2 == 0);
        String populatedTemplate = template;
        for (int i = 0; i < values.length; i += 2) {
            populatedTemplate = populatedTemplate.replace(values[i], values[i + 1]);
        }
        return populatedTemplate;
    }
    
    public static class EmailTemplates {
        public static final String USER_COURSE_JOIN =
                FileHelper.readResourceFile("userEmailTemplate-courseJoin.html");
        public static final String FRAGMENT_STUDENT_COURSE_JOIN =
                FileHelper.readResourceFile("studentEmailFragment-courseJoin.html");
        public static final String FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET =
                FileHelper.readResourceFile("studentEmailFragment-googleIdReset.html");
        public static final String FRAGMENT_INSTRUCTOR_COURSE_JOIN =
                FileHelper.readResourceFile("instructorEmailFragment-courseJoin.html");
        public static final String USER_FEEDBACK_SESSION =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSession.html");
        public static final String USER_FEEDBACK_SESSION_CLOSING =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionClosing.html");
        public static final String USER_FEEDBACK_SESSION_PUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionPublished.html");
        public static final String USER_PENDING_COMMENTS_CLEARED =
                FileHelper.readResourceFile("userEmailTemplate-pendingCommentsCleared.html");
        public static final String SYSTEM_ERROR =
                FileHelper.readResourceFile("systemErrorEmailTemplate.html");
        public static final String NEW_INSTRUCTOR_ACCOUNT_WELCOME =
                FileHelper.readResourceFile("newInstructorAccountWelcome.html");
    }
    
    public static class FeedbackQuestion {

        public static class FormTemplates {
            public static final String FEEDBACK_QUESTION_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionAdditionalInfoTemplate.html");

            public static final String TEXT_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionTextSubmissionFormTemplate.html");
            public static final String TEXT_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionTextResultStatsTemplate.html");

            public static final String MCQ_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionMcqSubmissionFormTemplate.html");
            public static final String MCQ_SUBMISSION_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqSubmissionFormOptionFragment.html");
            public static final String MCQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqSubmissionFormOtherOptionFragment.html");
            public static final String MCQ_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionMcqEditFormTemplate.html");
            public static final String MCQ_EDIT_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqEditFormOptionFragment.html");
            public static final String MCQ_ADDITIONAL_INFO_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqAdditionalInfoFragment.html");
            public static final String MCQ_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionMcqAdditionalInfoTemplate.html");
            public static final String MCQ_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultStatsTemplate.html");
            public static final String MCQ_RESULT_STATS_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultStatsOptionFragment.html");

            public static final String MSQ_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionMsqSubmissionFormTemplate.html");
            public static final String MSQ_SUBMISSION_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMsqSubmissionFormOptionFragment.html");
            public static final String MSQ_SUBMISSION_FORM_OTHEROPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMsqSubmissionFormOtherOptionFragment.html");
            public static final String MSQ_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionMsqEditFormTemplate.html");
            public static final String MSQ_EDIT_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMsqEditFormOptionFragment.html");
            public static final String MSQ_ADDITIONAL_INFO_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMsqAdditionalInfoFragment.html");
            public static final String MSQ_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionMsqAdditionalInfoTemplate.html");

            public static final String NUMSCALE_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionNumScaleEditFormTemplate.html");
            public static final String NUMSCALE_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionNumScaleSubmissionFormTemplate.html");
            public static final String NUMSCALE_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionNumScaleResultStatsTemplate.html");
            public static final String NUMSCALE_RESULTS_STATS_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionNumScaleResultsStatsFragment.html");
            public static final String NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE =
                    FileHelper
                            .readResourceFile("feedbackQuestionNumScaleResultStatsTemplateWithSelfResponse.html");
            public static final String NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE =
                    FileHelper
                            .readResourceFile("feedbackQuestionNumScaleResultsStatsFragmentWithSelfResponse.html");

            public static final String CONSTSUM_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionConstSumSubmissionFormTemplate.html");
            public static final String CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionConstSumSubmissionFormOptionFragment.html");
            public static final String CONSTSUM_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionConstSumEditFormTemplate.html");
            public static final String CONSTSUM_EDIT_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionConstSumEditFormOptionFragment.html");
            public static final String CONSTSUM_RESULT_OPTION_STATS =
                    FileHelper.readResourceFile("feedbackQuestionConstSumResultStatsTemplate.html");
            public static final String CONSTSUM_RESULT_STATS_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionConstSumResultStatsOptionFragment.html");
            public static final String CONSTSUM_RESULT_RECIPIENT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionConstSumResultStatsRecipientTemplate.html");
            public static final String CONSTSUM_RESULT_STATS_RECIPIENTFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionConstSumResultStatsRecipientFragment.html");

            public static final String CONTRIB_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionContribAdditionalInfoTemplate.html");
            public static final String CONTRIB_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionContribEditFormTemplate.html");
            public static final String CONTRIB_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionContribSubmissionFormTemplate.html");
            public static final String CONTRIB_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionContribResultStatsTemplate.html");
            public static final String CONTRIB_RESULT_STATS_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionContribResultStatsFragment.html");
            public static final String CONTRIB_RESULT_STATS_STUDENT =
                    FileHelper.readResourceFile("feedbackQuestionContribResultStatsStudentViewTemplate.html");
            public static final String CONTRIB_RESULT_STATS_STUDENT_INFO =
                    FileHelper
                            .readResourceFile("feedbackQuestionContribResultStatsStudentViewAdditionalInfo.html");

            public static final String RUBRIC_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormTemplate.html");
            public static final String RUBRIC_SUBMISSION_FORM_MOBILE_PANEL_FRAGMENT =
                    FileHelper
                            .readResourceFile("feedbackQuestionRubricSubmissionFormMobilePanelFragment.html");
            public static final String RUBRIC_SUBMISSION_FORM_MOBILE_PANEL =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormMobilePanel.html");
            public static final String RUBRIC_SUBMISSION_FORM_HEADER_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormHeaderFragment.html");
            public static final String RUBRIC_SUBMISSION_FORM_BODY_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormBodyFragment.html");
            public static final String RUBRIC_SUBMISSION_FORM_BODY =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormBody.html");
            public static final String RUBRIC_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormTemplate.html");
            public static final String RUBRIC_EDIT_FORM_HEADER_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormHeaderFragment.html");
            public static final String RUBRIC_EDIT_FORM_WEIGHT_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormWeightFragment.html");
            public static final String RUBRIC_EDIT_FORM_BODY_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormBodyFragment.html");
            public static final String RUBRIC_EDIT_FORM_BODY =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormBody.html");
            public static final String RUBRIC_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsTemplate.html");
            public static final String RUBRIC_RESULT_STATS_HEADER_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsHeaderFragment.html");
            public static final String RUBRIC_RESULT_STATS_BODY_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsBodyFragment.html");
            public static final String RUBRIC_RESULT_STATS_BODY =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsBody.html");
            public static final String RUBRIC_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionRubricAdditionalInfoTemplate.html");

            public static final String RANK_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionRankSubmissionFormTemplate.html");
            public static final String RANK_SUBMISSION_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRankSubmissionFormOptionFragment.html");
            public static final String RANK_EDIT_RECIPIENTS_FORM =
                    FileHelper.readResourceFile("feedbackQuestionRankRecipientsEditFormTemplate.html");
            public static final String RANK_EDIT_OPTIONS_FORM =
                    FileHelper.readResourceFile("feedbackQuestionRankOptionsEditFormTemplate.html");
            public static final String RANK_EDIT_FORM_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRankEditFormOptionFragment.html");
            public static final String RANK_RESULT_OPTION_STATS =
                    FileHelper.readResourceFile("feedbackQuestionRankResultStatsTemplate.html");
            public static final String RANK_RESULT_STATS_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRankResultStatsOptionFragment.html");
            public static final String RANK_RESULT_RECIPIENT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionRankResultStatsRecipientTemplate.html");
            public static final String RANK_RESULT_STATS_RECIPIENTFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRankResultStatsRecipientFragment.html");
        }
        
        public static class Slots {
            
            public static final String QUESTION_INDEX = "${questionIndex}";
            public static final String RESPONSE_INDEX = "${responseIndex}";
            public static final String DISABLED = "${disabled}";
            public static final String FEEDBACK_RESPONSE_TEXT = "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}";
            public static final String CHECKED = "${checked}";
            public static final String OPTION_RECIPIENT_DISPLAY_NAME = "${optionRecipientDisplayName}";
            public static final String QUESTION_NUMBER = "${questionNumber}";

            // MCQ
            public static final String MCQ_CHOICE_VALUE = "${mcqChoiceValue}";

            // MSQ
            public static final String MSQ_CHOICE_VALUE = "${msqChoiceValue}";
            public static final String MSQ_CHOICE_TEXT = "${msqChoiceText}";

            // Numscale
            public static final String MIN_SCALE = "${minScale}";
            public static final String MAX_SCALE = "${maxScale}";
            public static final String STEP = "${step}";
            public static final String NUMSCALE_MIN = "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN}";
            public static final String NUMSCALE_MAX = "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX}";
            public static final String NUMSCALE_STEP = "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP}";

            // Constant Sum
            public static final String OPTION_INDEX = "${optionIdx}";
            public static final String CONSTSUM_OPTION_VISIBILITY = "${constSumOptionVisibility}";
            public static final String CONSTSUM_OPTION_POINT = "${constSumOptionPoint}";
            public static final String CONSTSUM_OPTION_VALUE = "${constSumOptionValue}";
            public static final String CONSTSUM_POINTS = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}";
            public static final String CONSTSUM_TO_RECIPIENTS_VALUE = "${constSumToRecipientsValue}";
            public static final String CONSTSUM_TO_RECIPIENTS = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}";
            public static final String CONSTSUM_DISTRIBUTE_UNEVENLY =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}";
            public static final String CONSTSUM_POINTS_PER_OPTION =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}";

            // Rank
            public static final String RANK_OPTION_VISIBILITY = "${rankOptionVisibility}";
            public static final String RANK_OPTION_VALUE = "${rankOptionValue}";

            // Rubric
            public static final String CURRENT_ROWS = "${currRows}";
            public static final String CURRENT_COLS = "${currCols}";
            public static final String TABLE_HEADER_ROW_FRAGMENT_HTML = "${tableHeaderRowFragmentHtml}";
            public static final String TABLE_BODY_HTML = "${tableBodyHtml}";
            public static final String ROW = "${row}";
            public static final String COL = "${col}";
            public static final String RUBRIC_CHOICE_VALUE = "${rubricChoiceValue}";
        }
    }
    
    // TODO: Consider adding instructions for the feedback session into template?
    // TODO: Or simply use static strings here?
    public static class FeedbackSessionTemplates {
        public static final String TEAM_EVALUATION =
                FileHelper.readResourceFile("feedbackSessionTeamEvaluationTemplate.json");
    }
}
