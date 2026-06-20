import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  ConstsumRecipientRow,
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumRecipientsQuestionDetails,
  FeedbackQuestionType,
  QuestionOutput,
} from '../api-output';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

/**
 * Concrete implementation of {@link FeedbackConstantSumRecipientsQuestionDetails}.
 */
export class FeedbackConstantSumRecipientsQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackConstantSumRecipientsQuestionDetails
{
  pointsPerOption = false;
  forceUnevenDistribution = false;
  distributePointsFor: string = FeedbackConstantSumDistributePointsType.NONE;
  points = 100;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;
  minPoint: number | undefined = undefined;
  maxPoint: number | undefined = undefined;

  constructor(apiOutput: FeedbackConstantSumRecipientsQuestionDetails) {
    super();
    this.pointsPerOption = apiOutput.pointsPerOption;
    this.forceUnevenDistribution = apiOutput.forceUnevenDistribution;
    this.distributePointsFor = apiOutput.distributePointsFor;
    this.points = apiOutput.points;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isConstsumRecipients(stats) || stats.rows.length === 0) {
      return [];
    }

    const header: string[] = [
      'Team',
      'Recipient',
      'Recipient Email',
      'Total Points',
      'Average Points',
      'Points Received',
    ];
    const dataRows: string[][] = stats.rows.map((row: ConstsumRecipientRow) => [
      row.recipientTeam,
      row.recipientName,
      row.recipientEmail ?? '',
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
