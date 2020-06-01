import {
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackTextQuestionDetails}.
 */
export class FeedbackTextQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackTextQuestionDetails {

  recommendedLength: number = 0;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.TEXT;

  constructor(apiOutput: FeedbackTextQuestionDetails) {
    super();
    this.recommendedLength = apiOutput.recommendedLength;
    this.questionText = apiOutput.questionText;
  }
}
