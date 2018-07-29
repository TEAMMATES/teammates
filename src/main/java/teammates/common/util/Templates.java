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
        public static final String USER_FEEDBACK_SESSION =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSession.html");
        public static final String USER_FEEDBACK_SESSION_PUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionPublished.html");
        public static final String USER_FEEDBACK_SUBMISSION_CONFIRMATION =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSubmissionConfirmation.html");
        public static final String USER_FEEDBACK_SESSION_UNPUBLISHED =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionUnpublished.html");
        public static final String FRAGMENT_SINGLE_FEEDBACK_SESSION_LINKS =
                FileHelper.readResourceFile("userEmailTemplateFragment-feedbackSessionResendAllLinks.html");
        public static final String USER_FEEDBACK_SESSION_RESEND_ALL_LINKS =
                FileHelper.readResourceFile("userEmailTemplate-feedbackSessionResendAllLinks.html");
        public static final String SEVERE_ERROR_LOG_LINE =
                FileHelper.readResourceFile("severeErrorLogLine.html");
        public static final String NEW_INSTRUCTOR_ACCOUNT_WELCOME =
                FileHelper.readResourceFile("newInstructorAccountWelcome.html");
        public static final String FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION =
                FileHelper.readResourceFile("userEmailFragment-sessionAdditionalContactInformationFragment.html");
    }

    public static class FeedbackQuestion {

        public static class FormTemplates {
            public static final String FEEDBACK_QUESTION_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionAdditionalInfoTemplate.html");

            public static final String TEXT_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionTextSubmissionFormTemplate.html");
            public static final String TEXT_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionTextResultStatsTemplate.html");
            public static final String TEXT_EDIT_FORM =
                    FileHelper.readResourceFile("feedbackQuestionTextEditFormTemplate.html");

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
            public static final String MCQ_EDIT_FORM_WEIGHTFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqEditFormWeightFragment.html");
            public static final String MCQ_ADDITIONAL_INFO_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqAdditionalInfoFragment.html");
            public static final String MCQ_ADDITIONAL_INFO =
                    FileHelper.readResourceFile("feedbackQuestionMcqAdditionalInfoTemplate.html");
            public static final String MCQ_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultStatsTemplate.html");
            public static final String MCQ_RESULT_STATS_OPTIONFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultStatsOptionFragment.html");
            public static final String MCQ_RESULT_RECIPIENT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultRecipientStatsTemplate.html");
            public static final String MCQ_RESULT_RECIPIENT_STATS_HEADER_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultRecipientStatsHeaderFragment.html");
            public static final String MCQ_RESULT_RECIPIENT_STATS_BODY_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultRecipientStatsBodyFragment.html");
            public static final String MCQ_RESULT_RECIPIENT_STATS_BODY_ROW_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMcqResultRecipientStatsBodyRowFragment.html");

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
            public static final String MSQ_EDIT_FORM_WEIGHTFRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionMsqEditFormWeightFragment.html");
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
                    FileHelper.readResourceFile("feedbackQuestionNumScaleResultStatsTemplateWithSelfResponse.html");
            public static final String NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE =
                    FileHelper.readResourceFile("feedbackQuestionNumScaleResultsStatsFragmentWithSelfResponse.html");

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
                    FileHelper.readResourceFile("feedbackQuestionContribResultStatsStudentViewAdditionalInfo.html");

            public static final String RUBRIC_SUBMISSION_FORM =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormTemplate.html");
            public static final String RUBRIC_SUBMISSION_FORM_MOBILE_PANEL_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricSubmissionFormMobilePanelFragment.html");
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
            public static final String RUBRIC_EDIT_FORM_TABLE_OPTIONS =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormTableOptions.html");
            public static final String RUBRIC_EDIT_FORM_TABLE_OPTIONS_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricEditFormTableOptionsFragment.html");
            public static final String RUBRIC_RESULT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsTemplate.html");
            public static final String RUBRIC_RESULT_STATS_HEADER_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsHeaderFragment.html");
            public static final String RUBRIC_RESULT_STATS_BODY_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsBodyFragment.html");
            public static final String RUBRIC_RESULT_STATS_BODY =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultStatsBody.html");
            public static final String RUBRIC_RESULT_RECIPIENT_STATS =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultRecipientStatsTemplate.html");
            public static final String RUBRIC_RESULT_RECIPIENT_STATS_HEADER_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultRecipientStatsHeaderFragment.html");
            public static final String RUBRIC_RESULT_RECIPIENT_STATS_BODY_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultRecipientStatsBodyFragment.html");
            public static final String RUBRIC_RESULT_RECIPIENT_STATS_BODY_ROW_FRAGMENT =
                    FileHelper.readResourceFile("feedbackQuestionRubricResultRecipientStatsBodyRowFragment.html");
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
            public static final String DESCRIPTION = "${description}";
            public static final String FEEDBACK_RESPONSE_TEXT = "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}";
            public static final String CHECKED = "${checked}";
            public static final String OPTION_RECIPIENT_DISPLAY_NAME = "${optionRecipientDisplayName}";
            public static final String NUMBER_OF_CHOICE_CREATED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}";
            public static final String QUESTION_NUMBER = "${questionNumber}";
            public static final String CHECKED_OTHER_OPTION_ENABLED = "${checkedOtherOptionEnabled}";
            public static final String MCQ_GENERATED_OPTIONS =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS}";
            public static final String MSQ_GENERATED_OPTIONS =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS}";

            public static final String GENERATE_OPTIONS_FOR_VALUE = "${generateOptionsForValue}";
            public static final String STUDENT_SELECTED = "${studentSelected}";
            public static final String STUDENT_EXCLUDING_SELF_SELECTED = "${studentExcludingSelfSelected}";
            public static final String TEAM_SELECTED = "${teamSelected}";
            public static final String TEAM_EXCLUDING_SELF_SELECTED = "${teamExcludingSelfSelected}";
            public static final String INSTRUCTOR_SELECTED = "${instructorSelected}";
            public static final String STUDENTS_TO_STRING = "${FeedbackParticipantType.STUDENTS.toString()}";
            public static final String STUDENTS_EXCLUDING_SELF_TO_STRING =
                    "${FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toString()}";
            public static final String TEAMS_TO_STRING = "${FeedbackParticipantType.TEAMS.toString()}";
            public static final String TEAMS_EXCLUDING_SELF_TO_STRING =
                    "${FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toString()}";
            public static final String INSTRUCTORS_TO_STRING = "${FeedbackParticipantType.INSTRUCTORS.toString()}";
            public static final String QUESTION_ADDITIONAL_INFO = "${questionAdditionalInfo}";
            public static final String ADDITIONAL_INFO_ID = "${additionalInfoId}";
            public static final String LESS = "${less}";
            public static final String MORE = "${more}";
            public static final String TEXT_DISABLED = "${text-disabled}";
            public static final String QUESTION_TYPE_NAME = "${questionTypeName}";
            public static final String COUNT = "${count}";
            public static final String PERCENTAGE = "${percentage}";
            public static final String WEIGHTED_PERCENTAGE = "${weightedPercentage}";
            public static final String AVERAGE = "${Average}";
            public static final String MAX = "${Max}";
            public static final String MIN = "${Min}";
            public static final String FRAGMENTS = "${fragments}";
            public static final String EXISTING_ANSWER = "${existingAnswer}";
            public static final String POSSIBLE_VALUES_STRING = "${possibleValuesString}";
            public static final String POSSIBLE_VALUES = "${possibleValues}";
            public static final String RECIPIENT_TEAM = "${recipientTeam}";
            public static final String RECIPIENT_NAME = "${recipientName}";
            public static final String SUMMARY_TITLE = "${summaryTitle}";
            public static final String STATS_TITLE = "${statsTitle}";
            public static final String STATS_FRAGMENTS = "${statsFragments}";
            public static final String OPTION_INDEX = "${optionIdx}";
            public static final String OPTIONS = "${options}";
            public static final String TEAM = "${team}";
            public static final String MOBILE_HTML = "${mobileHtml}";
            public static final String PANEL_BODY = "${panelBody}";
            public static final String ITERATOR = "${i}";
            public static final String IS_SESSION_OPEN = "${isSessionOpen}";

            // TEXT
            public static final String TEXT_EXISTING_RESPONSE = "${existingResponse}";

            // MCQ
            public static final String MCQ_CHOICE_VALUE = "${mcqChoiceValue}";
            public static final String MCQ_WEIGHT = "${mcqWeight}";
            public static final String MCQ_OTHER_WEIGHT = "${mcqOtherWeight}";
            public static final String MCQ_ASSIGN_WEIGHT_CHECKBOX = "${mcqAssignWeightsCheckbox}";
            public static final String MCQ_OTHER_OPTION_ANSWER = "${mcqOtherOptionAnswer}";
            public static final String MCQ_CHECKED_GENERATED_OPTION = "${checkedGeneratedOptions}";
            public static final String MCQ_SUBMISSION_FORM_OPTION_FRAGMENTS = "${mcqSubmissionFormOptionFragments}";
            public static final String MCQ_PARAM_IS_OTHER_OPTION_ANSWER =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER}";
            public static final String MCQ_EDIT_FORM_OPTION_FRAGMENTS = "${mcqEditFormOptionFragments}";
            public static final String MCQ_EDIT_FORM_WEIGHT_FRAGMENTS = "${mcqEditFormWeightFragments}";
            public static final String MCQ_NUM_OF_MCQ_CHOICES = "${numOfMcqChoices}";
            public static final String MCQ_ADDITIONAL_INFO_FRAGMENTS = "${mcqAdditionalInfoFragments}";
            public static final String MCQ_PARAM_CHOICE = "${Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE}";
            public static final String MCQ_PARAM_WEIGHT = "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT}";
            public static final String MCQ_PARAM_OTHER_WEIGHT = "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT}";
            public static final String MCQ_PARAM_HAS_ASSIGN_WEIGHT =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED}";
            public static final String MCQ_TOOLTIPS_ASSIGN_WEIGHT =
                    "${Const.Tooltips.FEEDBACK_QUESTION_MCQ_ASSIGN_WEIGHTS}";
            public static final String MCQ_PARAM_OTHER_OPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTION}";
            public static final String MCQ_PARAM_OTHER_OPTION_FLAG =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG}";
            public static final String MCQ_RECIPIENT_STAT_CELL = "${recipientStatCell}";
            public static final String MCQ_RECIPIENT_STATS_HTML = "${perRecipientStatsHtml}";
            public static final String MCQ_RECIPIENT_STAT_ROW = "${recipientStatRow}";

            // MSQ
            public static final String MSQ_CHOICE_VALUE = "${msqChoiceValue}";
            public static final String MSQ_WEIGHT = "${msqWeight}";
            public static final String MSQ_OTHER_WEIGHT = "${msqOtherWeight}";
            public static final String MSQ_ASSIGN_WEIGHT_CHECKBOX = "${msqAssignWeightsCheckbox}";
            public static final String MSQ_CHOICE_TEXT = "${msqChoiceText}";
            public static final String MSQ_OTHER_OPTION_ANSWER = "${msqOtherOptionAnswer}";
            public static final String MSQ_SUBMISSION_FORM_OPTION_FRAGMENTS = "${msqSubmissionFormOptionFragments}";
            public static final String MSQ_NUMBER_OF_CHOICES = "${numOfMsqChoices}";
            public static final String MSQ_CHECKED_GENERATED_OPTIONS = "${checkedGeneratedOptions}";
            public static final String MSQ_ADDITIONAL_INFO_FRAGMENTS = "${msqAdditionalInfoFragments}";
            public static final String MSQ_MAX_SELECTABLE_CHOICES = "${msqMaxSelectableChoices}";
            public static final String MSQ_MIN_SELECTABLE_CHOICES = "${msqMinSelectableChoices}";
            public static final String MSQ_IS_MAX_SELECTABLE_CHOICES_ENABLED =
                    "${isMaxSelectableChoicesEnabled}";
            public static final String MSQ_IS_MIN_SELECTABLE_CHOICES_ENABLED =
                    "${isMinSelectableChoicesEnabled}";
            public static final String MSQ_DISPLAY_MAX_SELECTABLE_CHOICES_HINT =
                    "${displayMaxSelectableChoicesHint}";
            public static final String MSQ_DISPLAY_MIN_SELECTABLE_CHOICES_HINT =
                    "${displayMinSelectableChoicesHint}";
            public static final String MSQ_PARAM_ENABLED_MAX_SELECTABLE_CHOICES =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ENABLE_MAX_SELECTABLE_CHOICES}";
            public static final String MSQ_PARAM_ENABLED_MIN_SELECTABLE_CHOICES =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ENABLE_MIN_SELECTABLE_CHOICES}";
            public static final String MSQ_PARAM_MAX_SELECTABLE_CHOICES =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MAX_SELECTABLE_CHOICES}";
            public static final String MSQ_PARAM_MIN_SELECTABLE_CHOICES =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_MIN_SELECTABLE_CHOICES}";
            public static final String MSQ_PARAM_CHOICE = "${Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE}";
            public static final String MSQ_PARAM_OTHER_OPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTION}";
            public static final String MSQ_PARAM_OTHER_OPTION_FLAG =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG}";
            public static final String MSQ_EDIT_FORM_OPTION_FRAGMENTS = "${msqEditFormOptionFragments}";
            public static final String MSQ_EDIT_FORM_WEIGHT_FRAGMENTS = "${msqEditFormWeightFragments}";
            public static final String MSQ_PARAM_WEIGHT = "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT}";
            public static final String MSQ_PARAM_OTHER_WEIGHT = "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT}";
            public static final String MSQ_PARAM_HAS_ASSIGN_WEIGHT =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED}";
            public static final String MSQ_TOOLTIPS_ASSIGN_WEIGHT =
                    "${Const.Tooltips.FEEDBACK_QUESTION_MSQ_ASSIGN_WEIGHTS}";
            public static final String MSQ_PARAM_IS_OTHER_OPTION_ANSWER =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER}";

            // Numscale
            public static final String MIN_SCALE = "${minScale}";
            public static final String MAX_SCALE = "${maxScale}";
            public static final String STEP = "${step}";
            public static final String AVERAGE_EXCLUDING_SELF_RESPONSE = "${AverageExcludingSelfResponse}";
            public static final String NUMSCALE_MIN = "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN}";
            public static final String NUMSCALE_MAX = "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX}";
            public static final String NUMSCALE_STEP = "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP}";
            public static final String NUMSCALE_TOOLTIPS_MIN = "${Const.ToolTips.FEEDBACK_QUESTION_NUMSCALE_MIN}";
            public static final String NUMSCALE_TOOLTIPS_MAX = "${Const.ToolTips.FEEDBACK_QUESTION_NUMSCALE_MAX}";
            public static final String NUMSCALE_TOOLTIPS_STEP = "${Const.ToolTips.FEEDBACK_QUESTION_NUMSCALE_STEP}";

            // Constant Sum
            public static final String CONSTSUM_OPTION_VISIBILITY = "${constSumOptionVisibility}";
            public static final String CONSTSUM_OPTION_POINT = "${constSumOptionPoint}";
            public static final String CONSTSUM_OPTION_VALUE = "${constSumOptionValue}";
            public static final String MARGIN_LEFT = "${marginLeft}";
            public static final String CONSTSUM_PARAM_POINTS = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}";
            public static final String CONSTSUM_PARAM_POINTSFOREACHOPTION =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION}";
            public static final String CONSTSUM_PARAM_POINTSFOREACHRECIPIENT =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT}";
            public static final String CONSTSUM_TO_RECIPIENTS_VALUE = "${constSumToRecipientsValue}";
            public static final String CONSTSUM_TO_RECIPIENTS =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}";
            public static final String CONSTSUM_SUBMISSION_FORM_OPTION_FRAGMENT = "${constSumSubmissionFormOptionFragments}";
            public static final String CONSTSUM_EDIT_FORM_OPTION_FRAGMENT = "${constSumEditFormOptionFragments}";
            public static final String CONSTSUM_NUM_OPTION_VALUE = "${constSumNumOptionValue}";
            public static final String CONSTSUM_POINTS_PER_OPTION_VALUE = "${constSumPointsPerOptionValue}";
            public static final String CONSTSUM_NUM_OPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}";
            public static final String CONSTSUM_POINTS_VALUE = "${constSumPointsValue}";
            public static final String CONSTSUM_NUMBER_OF_OPTIONS = "${numOfConstSumOptions}";
            public static final String CONSTSUM_SELECTED_POINTS_PER_OPTION = "${selectedConstSumPointsPerOption}";
            public static final String CONSTSUM_OPTION_TABLE_VISIBILITY = "${constSumOptionTableVisibility}";
            public static final String CONSTSUM_UNEVEN_DISTRIBUTION_VALUE = "${constSumUnevenDistributionValue}";
            public static final String CONSTSUM_POINTS = "${constSumPoints}";
            public static final String CONSTSUM_POINTS_RECEIVED = "${pointsReceived}";
            public static final String CONSTSUM_AVERAGE_POINTS = "${averagePoints}";
            public static final String CONSTSUM_TOTAL_POINTS = "${totalPoints}";
            public static final String CONSTSUM_PARAM_OPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}";
            public static final String CONSTSUM_DISTRIBUTE_UNEVENLY = "${distributeUnevenly}";
            public static final String CONSTSUM_PARAM_DISTRIBUTE_UNEVENLY =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}";
            public static final String CONSTSUM_DISTRIBUTE_POINTS_OPTIONS =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS}";
            public static final String CONSTSUM_DISTRIBUTE_POINTS_FOR_VALUE = "${distributePointsForValue}";
            public static final String CONSTSUM_DISTRIBUTE_ALL_UNEVENLY_SELECTED = "${distributeAllUnevenlySelected}";
            public static final String CONSTSUM_DISTRIBUTE_SOME_UNEVENLY_SELECTED = "${distributeSomeUnevenlySelected}";
            public static final String CONSTSUM_DISTRIBUTE_ALL_UNEVENLY_TO_STRING =
                    "${FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption()}";
            public static final String CONSTSUM_DISTRIBUTE_SOME_UNEVENLY_TO_STRING =
                    "${FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption()}";
            public static final String CONSTSUM_POINTS_PER_OPTION =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}";
            public static final String CONSTSUM_TOOLTIP_POINTS = "${Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTS}";
            public static final String CONSTSUM_TOOLTIP_POINTS_PER_OPTION =
                    "${Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION}";
            public static final String CONSTSUM_TOOLTIP_POINTS_PER_RECIPIENT =
                    "${Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT}";
            public static final String OPTION_DISPLAY = "${optionDisplay}";
            public static final String RECIPIENT_DISPLAY = "${recipientDisplay}";
            public static final String PER_OPTION_CHECKED = "${perOptionChecked}";
            public static final String PER_RECIPIENT_CHECKED = "${perRecipientChecked}";

            // Contribution
            public static final String CONTRIB_SELECT_FRAGMENTS_HTML = "${contribSelectFragmentsHtml}";
            public static final String CONTRIB_IS_NOT_SURE_ALLOWED_CHECKED = "${isNotSureAllowedChecked}";
            public static final String CONTRIB_PARAM_IS_NOT_SURE_ALLOWED_CHECKED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED}";
            public static final String CONTRIB_ADDITIONAL_INFO = "${contribAdditionalInfo}";
            public static final String CONTRIB_MY_VIEW_OF_ME = "${myViewOfMe}";
            public static final String CONTRIB_MY_VIEW_OF_OTHERS = "${myViewOfOthers}";
            public static final String CONTRIB_TEAM_VIEW_OF_ME = "${teamViewOfMe}";
            public static final String CONTRIB_TEAM_VIEW_OF_OTHERS = "${teamViewOfOthers}";
            public static final String CONTRIB_STUDENT_TEAM = "${studentTeam}";
            public static final String CONTRIB_STUDENT_NAME = "${studentName}";
            public static final String CONTRIB_FRAGMENTS = "${contribFragments}";
            public static final String CONTRIB_CC = "${CC}";
            public static final String CONTRIB_PC = "${PC}";
            public static final String CONTRIB_DIFF = "${Diff}";
            public static final String CONTRIB_RR = "${RR}";
            public static final String CONTRIB_TOOLTIPS_CLAIMED = "${Const.Tooltips.CLAIMED}";
            public static final String CONTRIB_TOOLTIPS_PERCEIVED = "${Const.Tooltips.PERCEIVED}";
            public static final String CONTRIB_TOOLTIPS_DIFF = "${Const.Tooltips.FEEDBACK_CONTRIBUTION_DIFF}";
            public static final String CONTRIB_TOOLTIPS_POINTS_RECEIVED =
                    "${Const.Tooltips.FEEDBACK_CONTRIBUTION_POINTS_RECEIVED}";
            public static final String CONTRIB_PARAM_STUDENT_NAME = "${Const.ParamsNames.STUDENT_NAME}";
            public static final String CONTRIB_EQUAL_SHARE_HELP = "${equalShareHelp}";

            // Rank
            public static final String RANK_OPTION_VISIBILITY = "${rankOptionVisibility}";
            public static final String RANK_OPTION_VALUE = "${rankOptionValue}";
            public static final String RANK_TO_RECIPIENTS_VALUE = "${rankToRecipientsValue}";
            public static final String RANK_NUM_OPTION_VALUE = "${rankNumOptionValue}";
            public static final String RANK_NUM_OPTIONS = "${numOfRankOptions}";
            public static final String RANK_RECIEVED = "${ranksReceived}";
            public static final String RANK_SELF = "${selfRank}";
            public static final String RANK_OVERALL = "${overallRank}";
            public static final String RANK_EXCLUDING_SELF_OVERALL = "${overallRankExcludingSelf}";
            public static final String RANK_EDIT_FORM_OPTION_FRAGMENTS = "${rankEditFormOptionFragments}";
            public static final String RANK_ARE_DUPLICATES_ALLOWED_VALUE = "${areDuplicatesAllowedValue}";
            public static final String RANK_ARE_DUPLICATES_ALLOWED_CHECKED = "${areDuplicatesAllowedChecked}";
            public static final String RANK_SUBMISSION_FORM_OPTION_FRAGMENTS = "${rankSubmissionFormOptionFragments}";
            public static final String RANK_OPTION_RECIPIENT_DISPLAY_NAME = "${optionRecipientDisplayName}";
            public static final String RANK_MIN_OPTIONS_TO_BE_RANKED = "${minOptionsToBeRanked}";
            public static final String RANK_MAX_OPTIONS_TO_BE_RANKED = "${maxOptionsToBeRanked}";
            public static final String RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED = "${isMinOptionsToBeRankedEnabled}";
            public static final String RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED = "${isMaxOptionsToBeRankedEnabled}";
            public static final String RANK_MIN_RECIPIENTS_TO_BE_RANKED = "${minRecipientsToBeRanked}";
            public static final String RANK_MAX_RECIPIENTS_TO_BE_RANKED = "${maxRecipientsToBeRanked}";
            public static final String RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED = "${isMinRecipientsToBeRankedEnabled}";
            public static final String RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED = "${isMaxRecipientsToBeRankedEnabled}";
            public static final String RANK_PARAM_TO_RECIPIENT = "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}";
            public static final String RANK_PARAM_NUM_OPTION =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}";
            public static final String RANK_PARAM_IS_DUPLICATES_ALLOWED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}";
            public static final String RANK_PARAM_OPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION}";
            public static final String RANK_PARAM_NUMBER_OF_CHOICE_CREATED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}";
            public static final String RANK_PARAM_MIN_OPTIONS_CHECKBOX =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_OPTIONS_CHECKBOX}";
            public static final String RANK_PARAM_MIN_OPTIONS_TO_BE_RANKED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_OPTIONS_TO_BE_RANKED}";
            public static final String RANK_PARAM_MAX_OPTIONS_CHECKBOX =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_OPTIONS_CHECKBOX}";
            public static final String RANK_PARAM_MAX_OPTIONS_TO_BE_RANKED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_OPTIONS_TO_BE_RANKED}";
            public static final String RANK_PARAM_MIN_RECIPIENTS_CHECKBOX =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_CHECKBOX}";
            public static final String RANK_PARAM_MIN_RECIPIENTS_TO_BE_RANKED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_TO_BE_RANKED}";
            public static final String RANK_PARAM_MAX_RECIPIENTS_CHECKBOX =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_CHECKBOX}";
            public static final String RANK_PARAM_MAX_RECIPIENTS_TO_BE_RANKED =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_TO_BE_RANKED}";

            // Rubric
            public static final String CURRENT_ROWS = "${currRows}";
            public static final String CURRENT_COLS = "${currCols}";
            public static final String RUBRIC_ROW_BODY_FRAGMENTS = "${rubricRowBodyFragments}";
            public static final String TABLE_HEADER_ROW_FRAGMENT_HTML = "${tableHeaderRowFragmentHtml}";
            public static final String TABLE_BODY_HTML = "${tableBodyHtml}";
            public static final String TABLE_BODY_EXCLUDING_SELF_HTML = "${tableBodyExcludingSelfHtml}";
            public static final String EXCLUDING_SELF_OPTION_VISIBLE = "${isExcludingSelfOptionAvailable}";
            public static final String SUB_QUESTION = "${subQuestion}";
            public static final String ROW = "${row}";
            public static final String COL = "${col}";
            public static final String RUBRIC_PERCENTAGE_FREQUENCY_OR_AVERAGE = "${percentageFrequencyOrAverage}";
            public static final String CHECK_ASSIGN_WEIGHTS = "${checkAssignWeights}";
            public static final String RUBRIC_WEIGHT = "${rubricWeight}";
            public static final String RUBRIC_CHOICE_VALUE = "${rubricChoiceValue}";
            public static final String RUBRIC_TABLE_WEIGHT_ROW_FRAGMENT_HTML = "${tableWeightRowFragmentHtml}";
            public static final String RUBRIC_ADDITIONAL_INFO_FRAGMENTS = "${rubricAdditionalInfoFragments}";
            public static final String RUBRIC_RECIPIENT_STAT_CELL = "${recipientStatCell}";
            public static final String RUBRIC_RECIPIENT_STATS_HTML = "${perRecipientStatsHtml}";
            public static final String RUBRIC_RECIPIENT_STAT_ROW = "${recipientStatRow}";
            public static final String RUBRIC_TOOLTIPS_ASSIGN_WEIGHTS =
                    "${Const.Tooltips.FEEDBACK_QUESTION_RUBRIC_ASSIGN_WEIGHTS}";
            public static final String RUBRIC_PARAM_ASSIGN_WEIGHTS =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED}";
            public static final String RUBRIC_PARAM_NUM_ROWS = "${Const.ParamNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS}";
            public static final String RUBRIC_PARAM_NUM_COLS = "${Const.ParamNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS}";
            public static final String RUBRIC_PARAM_SUB_QUESTION =
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}";
            public static final String RUBRIC_PARAM_DESCRIPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}";
            public static final String RUBRIC_PARAM_WEIGHT = "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}";
            public static final String RUBRIC_PARAM_CHOICE = "${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}";
            public static final String RUBRIC_TABLE_OPTIONS = "${tableOptionsHtml}";
            public static final String RUBRIC_TABLE_OPTIONS_FRAGMENT = "${rubricColumnOptionsFragments}";
        }
    }

    // TODO: Consider adding instructions for the feedback session into template?
    // TODO: Or simply use static strings here?
    public static class FeedbackSessionTemplates {
        public static final String TEAM_EVALUATION =
                FileHelper.readResourceFile("feedbackSessionTeamEvaluationTemplate.json");
    }
}
