import { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails, FeedbackQuestionType } from '../api-output';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackMcqResponseDetails}.
 */
export class FeedbackMcqResponseDetailsImpl extends AbstractFeedbackResponseDetails<FeedbackMcqQuestionDetails>
    implements FeedbackMcqResponseDetails {

  answer: string = '';
  isOther: boolean = false;
  otherFieldContent: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;

  constructor(apiOutput: FeedbackMcqResponseDetails) {
    super();
    this.answer = apiOutput.answer;
    this.isOther = apiOutput.isOther;
    this.otherFieldContent = apiOutput.otherFieldContent;
  }

  getResponseCsvAnswers(_: FeedbackMcqQuestionDetails): string[][] {
    let answerStr: string = this.answer;
    if (this.isOther) {
      answerStr = this.otherFieldContent;
    }
    return [[answerStr]];
  }

}
