import { FeedbackQuestionDetails } from '../api-output';

/**
 * Abstract class for a response detail.
 */
export abstract class AbstractFeedbackResponseDetails<QuestionDetails extends FeedbackQuestionDetails> {

  /**
   * Gets response answer(s) for CSV.
   */
  abstract getResponseCsvAnswers(correspondingQuestionDetails: QuestionDetails): string[][];
}
