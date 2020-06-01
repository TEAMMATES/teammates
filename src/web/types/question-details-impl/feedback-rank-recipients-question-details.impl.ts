import {
  FeedbackQuestionType,
  FeedbackRankRecipientsQuestionDetails, QuestionOutput,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackRankRecipientsQuestionDetails}.
 */
export class FeedbackRankRecipientsQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackRankRecipientsQuestionDetails {

  maxOptionsToBeRanked: number = NO_VALUE;
  minOptionsToBeRanked: number = NO_VALUE;
  areDuplicatesAllowed: boolean = false;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.RANK_RECIPIENTS;

  constructor(apiOutput: FeedbackRankRecipientsQuestionDetails) {
    super();
    this.maxOptionsToBeRanked = apiOutput.maxOptionsToBeRanked;
    this.minOptionsToBeRanked = apiOutput.minOptionsToBeRanked;
    this.areDuplicatesAllowed = apiOutput.areDuplicatesAllowed;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(_: QuestionOutput): string[][] {
    // TODO
    return [];
  }

}
