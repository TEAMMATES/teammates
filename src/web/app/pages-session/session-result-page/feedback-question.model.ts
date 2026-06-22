import { FeedbackQuestion, FeedbackQuestionResultsStatistics, ResponseOutput } from '../../../types/api-output';

/**
 * Feedback question model.
 */
export interface FeedbackQuestionModel {
  feedbackQuestion: FeedbackQuestion;
  questionStatistics?: FeedbackQuestionResultsStatistics;
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
