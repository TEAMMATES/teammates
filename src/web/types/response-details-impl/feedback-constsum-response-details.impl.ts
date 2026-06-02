import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import {
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackConstantSumOptionsResponseDetails,
  FeedbackConstantSumRecipientsQuestionDetails,
  FeedbackConstantSumRecipientsResponseDetails,
  FeedbackQuestionType,
} from '../api-output';

/**
 * Concrete implementation of {@link FeedbackConstantSumOptionsResponseDetails}.
 */
export class FeedbackConstantSumOptionsResponseDetailsImpl
  extends AbstractFeedbackResponseDetails<FeedbackConstantSumOptionsQuestionDetails>
  implements FeedbackConstantSumOptionsResponseDetails
{
  answers: number[] = [];
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_OPTIONS;

  constructor(apiOutput: FeedbackConstantSumOptionsResponseDetails) {
    super();
    this.answers = apiOutput.answers;
  }

  getResponseCsvAnswers(_correspondingQuestionDetails: FeedbackConstantSumOptionsQuestionDetails): string[][] {
    const answers: string[] = this.answers.map(String);
    return [['', ...answers]];
  }
}

/**
 * Concrete implementation of {@link FeedbackConstantSumRecipientsResponseDetails}.
 */
export class FeedbackConstantSumRecipientsResponseDetailsImpl
  extends AbstractFeedbackResponseDetails<FeedbackConstantSumRecipientsQuestionDetails>
  implements FeedbackConstantSumRecipientsResponseDetails
{
  answers: number[] = [];
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;

  constructor(apiOutput: FeedbackConstantSumRecipientsResponseDetails) {
    super();
    this.answers = apiOutput.answers;
  }

  getResponseCsvAnswers(_correspondingQuestionDetails: FeedbackConstantSumRecipientsQuestionDetails): string[][] {
    const answerStr: string = this.answers.map(String).join('');
    return [[answerStr]];
  }
}
