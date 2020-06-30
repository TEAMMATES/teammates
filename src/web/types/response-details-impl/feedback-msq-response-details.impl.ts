import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails as FeedbackMsqResponseDetails,
  FeedbackQuestionType,
} from '../api-output';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackMsqResponseDetails}.
 */
export class FeedbackMsqResponseDetailsImpl extends AbstractFeedbackResponseDetails<FeedbackMsqQuestionDetails>
    implements FeedbackMsqResponseDetails {

  answers: string[] = [];
  isOther: boolean = false;
  otherFieldContent: string = '';
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
      if (this.answers.indexOf(choice) === -1) {
        answers.push('');
      } else {
        answers.push(choice);
      }
    }
    return [['', ...answers]];
  }

}
