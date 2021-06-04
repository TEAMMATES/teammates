import { QuestionTypeStructures } from './api-const';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
} from './api-output';

/**
 * Structure for default text question details.
 */
export const DEFAULT_TEXT_QUESTION_DETAILS: () => FeedbackTextQuestionDetails =
    (): FeedbackTextQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_TEXT_QUESTION_DETAILS);
    };

/**
 * Structure for default text question response details.
 */
export const DEFAULT_TEXT_RESPONSE_DETAILS: () => FeedbackTextResponseDetails =
    (): FeedbackTextResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_TEXT_RESPONSE_DETAILS);
    };

/**
 * Structure for default contribution question details.
 */
export const DEFAULT_CONTRIBUTION_QUESTION_DETAILS: () => FeedbackContributionQuestionDetails =
    (): FeedbackContributionQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_CONTRIBUTION_QUESTION_DETAILS);
    };

/**
 * Structure for default contribution question response details.
 */
export const DEFAULT_CONTRIBUTION_RESPONSE_DETAILS: () => FeedbackContributionResponseDetails =
    (): FeedbackContributionResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_CONTRIBUTION_RESPONSE_DETAILS);
    };

/**
 * Structure for default constant sum (among options) question details.
 */
export const DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS: () => FeedbackConstantSumQuestionDetails =
    (): FeedbackConstantSumQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS);
    };

/**
 * Structure for default constant sum (among recipient) question details.
 */
export const DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS: () => FeedbackConstantSumQuestionDetails =
    (): FeedbackConstantSumQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS);
    };

/**
 * Structure for default constant sum question response details.
 */
export const DEFAULT_CONSTSUM_RESPONSE_DETAILS: () => FeedbackConstantSumResponseDetails =
    (): FeedbackConstantSumResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_CONSTSUM_RESPONSE_DETAILS);
    };

/**
 * Structure for default numerical scale question details.
 */
export const DEFAULT_NUMSCALE_QUESTION_DETAILS: () => FeedbackNumericalScaleQuestionDetails =
    (): FeedbackNumericalScaleQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_NUMSCALE_QUESTION_DETAILS);
    };

/**
 * Structure for default numerical scale question response details.
 */
export const DEFAULT_NUMSCALE_RESPONSE_DETAILS: () => FeedbackNumericalScaleResponseDetails =
    (): FeedbackNumericalScaleResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_NUMSCALE_RESPONSE_DETAILS);
    };

/**
 * Structure for default MCQ question details.
 */
export const DEFAULT_MCQ_QUESTION_DETAILS: () => FeedbackMcqQuestionDetails =
    (): FeedbackMcqQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_MCQ_QUESTION_DETAILS);
    };

/**
 * Structure for default MCQ question response details.
 */
export const DEFAULT_MCQ_RESPONSE_DETAILS: () => FeedbackMcqResponseDetails =
    (): FeedbackMcqResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_MCQ_RESPONSE_DETAILS);
    };

/**
 * Structure for default MSQ question details.
 */
export const DEFAULT_MSQ_QUESTION_DETAILS: () => FeedbackMsqQuestionDetails =
    (): FeedbackMsqQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_MSQ_QUESTION_DETAILS);
    };

/**
 * Structure for default MSQ question response details.
 */
export const DEFAULT_MSQ_RESPONSE_DETAILS: () => FeedbackMsqResponseDetails =
    (): FeedbackMsqResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_MSQ_RESPONSE_DETAILS);
    };

/**
 * Structure for default rubric question details.
 */
export const DEFAULT_RUBRIC_QUESTION_DETAILS: () => FeedbackRubricQuestionDetails =
    (): FeedbackRubricQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_RUBRIC_QUESTION_DETAILS);
    };

/**
 * Structure for default rubric question response details.
 */
export const DEFAULT_RUBRIC_RESPONSE_DETAILS: () => FeedbackRubricResponseDetails =
    (): FeedbackRubricResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_RUBRIC_RESPONSE_DETAILS);
    };

/**
 * Structure for default rank options question details.
 */
export const DEFAULT_RANK_OPTIONS_QUESTION_DETAILS: () => FeedbackRankOptionsQuestionDetails =
    (): FeedbackRankOptionsQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_RANK_OPTIONS_QUESTION_DETAILS);
    };

/**
 * Structure for default rank options question response details.
 */
export const DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS: () => FeedbackRankOptionsResponseDetails =
    (): FeedbackRankOptionsResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS);
    };

/**
 * Structure for default rank recipients question details.
 */
export const DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS: () => FeedbackRankRecipientsQuestionDetails =
    (): FeedbackRankRecipientsQuestionDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS);
    };

/**
 * Structure for default rank recipients question response details.
 */
export const DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS: () => FeedbackRankRecipientsResponseDetails =
    (): FeedbackRankRecipientsResponseDetails => {
      return JSON.parse(QuestionTypeStructures.DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS);
    };
