import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  ConstsumOptionRow,
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackQuestionType,
  QuestionOutput,
} from '../api-output';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

/**
 * Concrete implementation of {@link FeedbackConstantSumOptionsQuestionDetails}.
 */
export class FeedbackConstantSumOptionsQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackConstantSumOptionsQuestionDetails
{
  constSumOptions: string[] = ['', ''];
  pointsPerOption = false;
  forceUnevenDistribution = false;
  distributePointsFor: string = FeedbackConstantSumDistributePointsType.NONE;
  points = 100;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
  minPoint: number | undefined = undefined;
  maxPoint: number | undefined = undefined;

  constructor(apiOutput: FeedbackConstantSumOptionsQuestionDetails) {
    super();
    this.constSumOptions = apiOutput.constSumOptions;
    this.pointsPerOption = apiOutput.pointsPerOption;
    this.forceUnevenDistribution = apiOutput.forceUnevenDistribution;
    this.distributePointsFor = apiOutput.distributePointsFor;
    this.points = apiOutput.points;
    this.questionText = apiOutput.questionText;
    this.minPoint = apiOutput.minPoint;
    this.maxPoint = apiOutput.maxPoint;
  }

  override getQuestionCsvHeaders(): string[] {
    return ['Feedback', ...this.constSumOptions];
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isConstsumOptions(stats) || stats.options.length === 0) {
      return [];
    }

    const header: string[] = ['Option', 'Total Points', 'Average Points', 'Points Received'];
    const dataRows: string[][] = stats.options.map((row: ConstsumOptionRow) => [
      row.option,
      String(row.total),
      String(row.average),
      ...row.pointsReceived.map(String),
    ]);

    return [header, ...dataRows];
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
