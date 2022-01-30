// tslint:disable-next-line:max-line-length
import { ConstsumOptionsQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/constsum-options-question-statistics-calculation';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackQuestionType,
  QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackConstantSumQuestionDetails}.
 */
export class FeedbackConstantSumOptionsQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackConstantSumQuestionDetails {

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

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const statsCalculation: ConstsumOptionsQuestionStatisticsCalculation =
        new ConstsumOptionsQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

    statsRows.push(['Option', 'Total Points', 'Average Points', 'Points Received']);

    Object.keys(statsCalculation.pointsPerOption).sort().forEach((option: string) => {
      statsRows.push([
        option,
        String(statsCalculation.totalPointsPerOption[option]),
        String(statsCalculation.averagePointsPerOption[option]),
        ...statsCalculation.pointsPerOption[option].map(String),
      ]);
    });

    return statsRows;
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
