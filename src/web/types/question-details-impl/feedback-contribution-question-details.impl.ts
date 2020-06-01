import {
  FeedbackContributionQuestionDetails as FeedbackContributionQuestionDetails,
  FeedbackQuestionType,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackContributionQuestionDetails}.
 */
export class FeedbackContributionQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackContributionQuestionDetails {

  isNotSureAllowed: boolean = true;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONTRIB;

  constructor(apiOutput: FeedbackContributionQuestionDetails) {
    super();
    this.isNotSureAllowed = apiOutput.isNotSureAllowed;
    this.questionText = apiOutput.questionText;
  }
}
