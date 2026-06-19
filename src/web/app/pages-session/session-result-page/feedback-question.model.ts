import {
  FeedbackQuestion,
  FeedbackQuestionRecipientResultsStatistics,
  ResponseOutput,
} from '../../../types/api-output';

/**
 * Feedback question model.
 */
export interface FeedbackQuestionModel {
  feedbackQuestion: FeedbackQuestion;
  questionStatistics?: FeedbackQuestionRecipientResultsStatistics;
  allResponses: ResponseOutput[];
  responsesToSelf: ResponseOutput[];
  responsesFromSelf: ResponseOutput[];
  otherResponses: ResponseOutput[][];
  isLoading: boolean;
  isLoaded: boolean;
  errorMessage?: string;
  hasResponseButNotVisibleForPreview: boolean;
  hasCommentNotVisibleForPreview: boolean;
}
