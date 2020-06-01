import {
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackRankOptionsQuestionDetails}.
 */
export class FeedbackRankOptionsQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackRankOptionsQuestionDetails {

  minOptionsToBeRanked: number = NO_VALUE;
  maxOptionsToBeRanked: number = NO_VALUE;
  areDuplicatesAllowed: boolean = false;
  options: string[] = [];
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.RANK_OPTIONS;

  constructor(apiOutput: FeedbackRankOptionsQuestionDetails) {
    super();
    this.minOptionsToBeRanked = apiOutput.minOptionsToBeRanked;
    this.maxOptionsToBeRanked = apiOutput.maxOptionsToBeRanked;
    this.areDuplicatesAllowed = apiOutput.areDuplicatesAllowed;
    this.options = apiOutput.options;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvHeaders(): string[] {
    const optionsHeader: string[] = this.options.map((_: string, index: number) => `Rank ${index + 1}`);
    return ['Feedback', ...optionsHeader];
  }
}
