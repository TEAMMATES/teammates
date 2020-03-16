import { FeedbackQuestion } from '../../../../types/api-output';

/**
 * The model for a question to copy.
 */
export interface QuestionToCopyCandidate {
  courseId: string;
  feedbackSessionName: string;
  question: FeedbackQuestion;

  isSelected: boolean;
}
