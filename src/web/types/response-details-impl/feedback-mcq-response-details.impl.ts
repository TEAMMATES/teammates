import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails, FeedbackQuestionType } from '../api-output';

/**
 * Concrete implementation of {@link FeedbackMcqResponseDetails}.
 */
export class FeedbackMcqResponseDetailsImpl extends AbstractFeedbackResponseDetails<FeedbackMcqQuestionDetails>
    implements FeedbackMcqResponseDetails {

  answer = '';
  isOther = false;
  otherFieldContent = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;

  constructor(apiOutput: FeedbackMcqResponseDetails) {
    super();
    this.answer = apiOutput.answer;
    this.isOther = apiOutput.isOther;
    this.otherFieldContent = apiOutput.otherFieldContent;
  }

  getResponseCsvAnswers(): string[][] {
    let answerStr: string = this.answer;
    if (this.isOther) {
      answerStr = this.otherFieldContent;
    }
    return [[answerStr]];
  }

}
