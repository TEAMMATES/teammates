package teammates.ui.constants;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.JsonUtils;

/**
 * Default data structures for all different question types.
 */
public enum QuestionTypeStructures {
    // CHECKSTYLE.OFF:JavadocVariable
    DEFAULT_CONTRIBUTION_QUESTION_DETAILS(new FeedbackContributionQuestionDetails("")),
    DEFAULT_CONTRIBUTION_RESPONSE_DETAILS(new FeedbackContributionResponseDetails()),
    DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS(getConstSumOptionsQuestionStruct()),
    DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS(getConstSumRecipientsQuestionStruct()),
    DEFAULT_CONSTSUM_RESPONSE_DETAILS(new FeedbackConstantSumResponseDetails()),
    DEFAULT_MCQ_QUESTION_DETAILS(new FeedbackMcqQuestionDetails("")),
    DEFAULT_MCQ_RESPONSE_DETAILS(new FeedbackMcqResponseDetails()),
    DEFAULT_MSQ_QUESTION_DETAILS(new FeedbackMsqQuestionDetails("")),
    DEFAULT_MSQ_RESPONSE_DETAILS(new FeedbackMsqResponseDetails()),
    DEFAULT_NUMSCALE_QUESTION_DETAILS(new FeedbackNumericalScaleQuestionDetails("")),
    DEFAULT_NUMSCALE_RESPONSE_DETAILS(new FeedbackNumericalScaleResponseDetails()),
    DEFAULT_RANK_OPTIONS_QUESTION_DETAILS(new FeedbackRankOptionsQuestionDetails("")),
    DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS(new FeedbackRankOptionsResponseDetails()),
    DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS(new FeedbackRankRecipientsQuestionDetails("")),
    DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS(new FeedbackRankRecipientsResponseDetails()),
    DEFAULT_RUBRIC_QUESTION_DETAILS(new FeedbackRubricQuestionDetails("")),
    DEFAULT_RUBRIC_RESPONSE_DETAILS(new FeedbackRubricResponseDetails()),
    DEFAULT_TEXT_QUESTION_DETAILS(new FeedbackTextQuestionDetails("")),
    DEFAULT_TEXT_RESPONSE_DETAILS(new FeedbackTextResponseDetails());
    // CHECKSTYLE.ON:JavadocVariable

    @JsonValue
    private final String value;

    QuestionTypeStructures(Object value) {
        this.value = JsonUtils.toCompactJson(value).replace("\"", "\\\"");
    }

    public String getValue() {
        return value;
    }

    private static FeedbackConstantSumQuestionDetails getConstSumOptionsQuestionStruct() {
        FeedbackConstantSumQuestionDetails details = new FeedbackConstantSumQuestionDetails("");
        details.setQuestionType(FeedbackQuestionType.CONSTSUM_OPTIONS);
        details.setConstSumOptions(Arrays.asList("", ""));
        details.setNumOfConstSumOptions(2);
        return details;
    }

    private static FeedbackConstantSumQuestionDetails getConstSumRecipientsQuestionStruct() {
        FeedbackConstantSumQuestionDetails details = new FeedbackConstantSumQuestionDetails("");
        details.setQuestionType(FeedbackQuestionType.CONSTSUM_RECIPIENTS);
        details.setDistributeToRecipients(true);
        return details;
    }

}
