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

/**
 * Sort criteria for questions.
 */
export enum SortBy {
  /**
   * Nothing.
   */
  NONE,

  /**
   * Course ID.
   */
  COURSE_ID,

  /**
   * Feedback session name.
   */
  FEEDBACK_SESSION_NAME,

  /**
   * Feedback question type.
   */
  QUESTION_TYPE,

  /**
   * Feedback question text (brief).
   */
  QUESTION_TEXT,
}

/**
 * Sort order for questions.
 */
export enum SortOrder {
  /**
   * Descending sort order.
   */
  DESC,

  /**
   * Ascending sort order
   */
  ASC,
}
