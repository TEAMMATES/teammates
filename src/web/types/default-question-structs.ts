import {
  FeedbackConstantSumDistributePointsType,
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
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
} from './api-output';
import { CONTRIBUTION_POINT_NOT_SUBMITTED, NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from './feedback-response-details';

/**
 * Structure for default text question details.
 */
export const DEFAULT_TEXT_QUESTION_DETAILS: () => FeedbackTextQuestionDetails =
    (): FeedbackTextQuestionDetails => {
      return {
        recommendedLength: 0,
        questionType: FeedbackQuestionType.TEXT,
        questionText: '',
      };
    };

/**
 * Structure for default text question response details.
 */
export const DEFAULT_TEXT_RESPONSE_DETAILS: () => FeedbackTextResponseDetails =
    (): FeedbackTextResponseDetails => {
      return {
        answer: '',
        questionType: FeedbackQuestionType.TEXT,
      };
    };

/**
 * Structure for default contribution question details.
 */
export const DEFAULT_CONTRIBUTION_QUESTION_DETAILS: () => FeedbackContributionQuestionDetails =
    (): FeedbackContributionQuestionDetails => {
      return {
        isNotSureAllowed: true,
        questionType: FeedbackQuestionType.CONTRIB,
        questionText: '',
      };
    };

/**
 * Structure for default contribution question response details.
 */
export const DEFAULT_CONTRIBUTION_RESPONSE_DETAILS: () => FeedbackContributionResponseDetails =
    (): FeedbackContributionResponseDetails => {
      return {
        answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
        questionType: FeedbackQuestionType.CONTRIB,
      };
    };

/**
 * Structure for default constant sum question details.
 */
export const DEFAULT_CONSTSUM_QUESTION_DETAILS: () => FeedbackConstantSumQuestionDetails =
    (): FeedbackConstantSumQuestionDetails => {
      return {
        numOfConstSumOptions: 0,
        constSumOptions: ['', ''],
        distributeToRecipients: false,
        pointsPerOption: false,
        forceUnevenDistribution: false,
        distributePointsFor: FeedbackConstantSumDistributePointsType.NONE,
        points: 100,
        questionType: FeedbackQuestionType.CONSTSUM,
        questionText: '',
      };
    };

/**
 * Structure for default constant sum question response details.
 */
export const DEFAULT_CONSTSUM_RESPONSE_DETAILS: () => FeedbackConstantSumResponseDetails =
    (): FeedbackConstantSumResponseDetails => {
      return {
        answers: [],
        questionType: FeedbackQuestionType.CONSTSUM,
      };
    };

/**
 * Structure for default numerical scale question details.
 */
export const DEFAULT_NUMSCALE_QUESTION_DETAILS: () => FeedbackNumericalScaleQuestionDetails =
    (): FeedbackNumericalScaleQuestionDetails => {
      return {
        minScale: 1,
        maxScale: 5,
        step: 0.5,
        questionType: FeedbackQuestionType.NUMSCALE,
        questionText: '',
      };
    };

/**
 * Structure for default numerical scale question response details.
 */
export const DEFAULT_NUMSCALE_RESPONSE_DETAILS: () => FeedbackNumericalScaleResponseDetails =
    (): FeedbackNumericalScaleResponseDetails => {
      return {
        answer: NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED,
        questionType: FeedbackQuestionType.NUMSCALE,
      };
    };

/**
 * Structure for default MCQ question details.
 */
export const DEFAULT_MCQ_QUESTION_DETAILS: () => FeedbackMcqQuestionDetails =
    (): FeedbackMcqQuestionDetails => {
      return {
        hasAssignedWeights: false,
        mcqWeights: [],
        mcqOtherWeight: 0,
        numOfMcqChoices: 0,
        mcqChoices: [],
        otherEnabled: false,
        generateOptionsFor: FeedbackParticipantType.NONE,
        questionType: FeedbackQuestionType.MCQ,
        questionText: '',
      };
    };

/**
 * Structure for default MCQ question response details.
 */
export const DEFAULT_MCQ_RESPONSE_DETAILS: () => FeedbackMcqResponseDetails =
    (): FeedbackMcqResponseDetails => {
      return {
        answer: '',
        isOther: false,
        otherFieldContent: '',
        questionType: FeedbackQuestionType.MCQ,
      };
    };

/**
 * Structure for default MSQ question details.
 */
export const DEFAULT_MSQ_QUESTION_DETAILS: () => FeedbackMsqQuestionDetails =
    (): FeedbackMsqQuestionDetails => {
      return {
        msqChoices: [],
        otherEnabled: false,
        generateOptionsFor: FeedbackParticipantType.NONE,
        maxSelectableChoices: Number.MIN_VALUE,
        minSelectableChoices: Number.MIN_VALUE,
        hasAssignedWeights: false,
        msqWeights: [],
        msqOtherWeight: 0,
        questionType: FeedbackQuestionType.MSQ,
        questionText: '',
      };
    };

/**
 * Structure for default MSQ question response details.
 */
export const DEFAULT_MSQ_RESPONSE_DETAILS: () => FeedbackMsqResponseDetails =
    (): FeedbackMsqResponseDetails => {
      return {
        answers: [],
        isOther: false,
        otherFieldContent: '',
        questionType: FeedbackQuestionType.MSQ,
      };
    };

/**
 * Structure for default rubric question details.
 */
export const DEFAULT_RUBRIC_QUESTION_DETAILS: () => FeedbackRubricQuestionDetails =
    (): FeedbackRubricQuestionDetails => {
      return {
        hasAssignedWeights: false,
        numOfRubricChoices: 0,
        rubricChoices: [],
        numOfRubricSubQuestions: 0,
        rubricSubQuestions: [],
        rubricWeightsForEachCell: [],
        rubricDescriptions: [],
        questionType: FeedbackQuestionType.RUBRIC,
        questionText: '',
      };
    };

/**
 * Structure for default rubric question response details.
 */
export const DEFAULT_RUBRIC_RESPONSE_DETAILS: () => FeedbackRubricResponseDetails =
    (): FeedbackRubricResponseDetails => {
      return {
        answer: [],
        questionType: FeedbackQuestionType.RUBRIC,
      };
    };

/**
 * Structure for default rank options question details.
 */
export const DEFAULT_RANK_OPTIONS_QUESTION_DETAILS: () => FeedbackRankOptionsQuestionDetails =
    (): FeedbackRankOptionsQuestionDetails => {
      return {
        minOptionsToBeRanked: Number.MIN_VALUE,
        maxOptionsToBeRanked: Number.MIN_VALUE,
        areDuplicatesAllowed: false,
        options: [],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
        questionText: '',
      };
    };

/**
 * Structure for default rank options question response details.
 */
export const DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS: () => FeedbackRankOptionsResponseDetails =
    (): FeedbackRankOptionsResponseDetails => {
      return {
        answers: [],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      };
    };

/**
 * Structure for default rank recipients question details.
 */
export const DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS: () => FeedbackRankRecipientsQuestionDetails =
    (): FeedbackRankRecipientsQuestionDetails => {
      return {
        minOptionsToBeRanked: Number.MIN_VALUE,
        maxOptionsToBeRanked: Number.MIN_VALUE,
        areDuplicatesAllowed: false,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
        questionText: '',
      };
    };

/**
 * Structure for default rank recipients question response details.
 */
export const DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS: () => FeedbackRankRecipientsResponseDetails =
    (): FeedbackRankRecipientsResponseDetails => {
      return {
        answer: 0,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      };
    };
