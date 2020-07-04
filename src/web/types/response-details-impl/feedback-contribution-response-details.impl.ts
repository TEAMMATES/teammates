import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackQuestionType,
} from '../api-output';
import { CONTRIBUTION_POINT_NOT_SUBMITTED, CONTRIBUTION_POINT_NOT_SURE } from '../feedback-response-details';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackContributionResponseDetails}.
 */
export class FeedbackContributionResponseDetailsImpl
    extends AbstractFeedbackResponseDetails<FeedbackContributionQuestionDetails>
    implements FeedbackContributionResponseDetails {

  answer: number = CONTRIBUTION_POINT_NOT_SUBMITTED;
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONTRIB;

  constructor(apiOutput: FeedbackContributionResponseDetails) {
    super();
    this.answer = apiOutput.answer;
  }

  getResponseCsvAnswers(_: FeedbackContributionQuestionDetails): string[][] {
    const answer: number = this.answer;
    let answerStr: string = '';
    if (answer > 100) {
      answerStr = `Equal share + ${answer - 100}%`; // Do more
    } else if (answer === 100) {
      answerStr = 'Equal share'; // Do same
    } else if (answer > 0) {
      answerStr = `Equal share - ${(100 - answer)}%`; // Do less
    } else if (answer === 0) {
      answerStr = '0%'; // Do none
    } else if (answer === CONTRIBUTION_POINT_NOT_SURE) {
      answerStr = 'Not Sure';
    } else {
      answerStr = '';
    }
    return [[answerStr]];
  }
}
