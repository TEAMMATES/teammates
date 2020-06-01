import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackQuestionType,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackConstantSumQuestionDetails}.
 */
export class FeedbackConstantSumOptionsQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackConstantSumQuestionDetails {

  numOfConstSumOptions: number = 2;
  constSumOptions: string[] = ['', ''];
  distributeToRecipients: boolean = false;
  pointsPerOption: boolean = false;
  forceUnevenDistribution: boolean = false;
  distributePointsFor: string = FeedbackConstantSumDistributePointsType.NONE;
  points: number = 100;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_OPTIONS;

  constructor(apiOutput: FeedbackConstantSumQuestionDetails) {
    super();
    this.numOfConstSumOptions = apiOutput.numOfConstSumOptions;
    this.constSumOptions = apiOutput.constSumOptions;
    this.pointsPerOption = apiOutput.pointsPerOption;
    this.forceUnevenDistribution = apiOutput.forceUnevenDistribution;
    this.distributePointsFor = apiOutput.distributePointsFor;
    this.points = apiOutput.points;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvHeaders(): string[] {
    return ['Feedback', ...this.constSumOptions];
  }

}
