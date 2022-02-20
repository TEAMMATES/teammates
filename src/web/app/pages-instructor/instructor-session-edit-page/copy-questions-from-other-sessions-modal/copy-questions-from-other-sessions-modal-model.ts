import { FeedbackQuestion } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';

/**
 * The model for a feedback session tab.
 */
export interface FeedbackSessionTabModel {
  courseId: string;
  feedbackSessionName: string;
  createdAtTimestamp: number;

  questionsTableRowModels: QuestionToCopyCandidate[];
  questionsTableRowModelsSortBy: SortBy;
  questionsTableRowModelsSortOrder: SortOrder;

  hasQuestionsLoaded: boolean;
  isTabExpanded: boolean;
  hasLoadingFailed: boolean;
}

/**
 * The model for a question to copy.
 */
export interface QuestionToCopyCandidate {
  question: FeedbackQuestion;
  isSelected: boolean;
}
