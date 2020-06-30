import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails as FeedbackConstantSumResponseDetails,
  FeedbackQuestionType,
} from '../api-output';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackConstantSumResponseDetails}.
 */
export class FeedbackConstantSumResponseDetailsImpl
    extends AbstractFeedbackResponseDetails<FeedbackConstantSumQuestionDetails>
    implements FeedbackConstantSumResponseDetails {

  answers: number[] = [];
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM;

  constructor(apiOutput: FeedbackConstantSumResponseDetails) {
    super();
    this.answers = apiOutput.answers;
  }

  getResponseCsvAnswers(correspondingQuestionDetails: FeedbackConstantSumQuestionDetails): string[][] {
    if (correspondingQuestionDetails.distributeToRecipients) {
      const answerStr: string = this.answers
          .map(String)
          .join('');
      return [[answerStr]];
    }

    const answers: string[] = this.answers.map(String);
    return [['', ...answers]];
  }
}
