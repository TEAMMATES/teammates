import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import { FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails, FeedbackQuestionType } from '../api-output';

/**
 * Concrete implementation of {@link FeedbackMsqResponseDetails}.
 */
export class FeedbackMsqResponseDetailsImpl
  extends AbstractFeedbackResponseDetails<FeedbackMsqQuestionDetails>
  implements FeedbackMsqResponseDetails
{
  answers: string[] = [];
  isOther = false;
  otherFieldContent = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MSQ;

  constructor(apiOutput: FeedbackMsqResponseDetails) {
    super();
    this.answers = apiOutput.answers;
    this.isOther = apiOutput.isOther;
    this.otherFieldContent = apiOutput.otherFieldContent;
  }

  getResponseCsvAnswers(correspondingQuestionDetails: FeedbackMsqQuestionDetails): string[][] {
    const isAnswerBlank: boolean = this.answers.length === 1 && this.answers[0].length === 0;
    if (isAnswerBlank) {
      return [['']];
    }
    const answers: string[] = [];
    for (const choice of correspondingQuestionDetails.msqChoices) {
      if (this.answers.includes(choice)) {
        answers.push(choice);
      } else {
        answers.push('');
      }
    }
    return [['', ...answers]];
  }
}
