import { FeedbackQuestion, ResponseOutput } from '../../../types/api-output';

/**
 * Feedback question model.
 */
export interface FeedbackQuestionModel {
  feedbackQuestion: FeedbackQuestion;
  questionStatistics: string;
  allResponses: ResponseOutput[];
  responsesToSelf: ResponseOutput[];
  responsesFromSelf: ResponseOutput[];
  otherResponses: ResponseOutput[][];
  isLoading: boolean;
  isLoaded: boolean;
  hasResponse: boolean;
  errorMessage?: string;
  hasResponseButNotVisibleForPreview: boolean;
  hasCommentNotVisibleForPreview: boolean;
}
