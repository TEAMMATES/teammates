import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import { FeedbackQuestionType, FeedbackRankRecipientsQuestionDetails, QuestionOutput } from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

/**
 * Concrete implementation of {@link FeedbackRankRecipientsQuestionDetails}.
 */
export class FeedbackRankRecipientsQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackRankRecipientsQuestionDetails
{
  maxOptionsToBeRanked: number = NO_VALUE;
  minOptionsToBeRanked: number = NO_VALUE;
  areDuplicatesAllowed = false;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.RANK_RECIPIENTS;

  constructor(apiOutput: FeedbackRankRecipientsQuestionDetails) {
    super();
    this.maxOptionsToBeRanked = apiOutput.maxOptionsToBeRanked;
    this.minOptionsToBeRanked = apiOutput.minOptionsToBeRanked;
    this.areDuplicatesAllowed = apiOutput.areDuplicatesAllowed;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isRankRecipients(stats) || stats.rows.length === 0) {
      return [];
    }

    const rows: string[][] = [
      [
        'Team',
        'Recipient',
        'Recipient Email',
        'Self Rank',
        'Overall Rank',
        'Overall Rank Excluding Self',
        'Team Rank',
        'Team Rank Excluding Self',
        'Ranks Received',
      ],
    ];

    const dash = '-';
    for (const row of stats.rows) {
      rows.push([
        row.recipientTeam,
        row.recipientName,
        row.recipientEmail ?? '',
        row.selfRank == null ? dash : String(row.selfRank),
        row.overallRank == null ? dash : String(row.overallRank),
        row.rankExcludingSelf == null ? dash : String(row.rankExcludingSelf),
        row.rankInTeam == null ? dash : String(row.rankInTeam),
        row.rankInTeamExcludingSelf == null ? dash : String(row.rankInTeamExcludingSelf),
        ...row.ranksReceived.map(String),
      ]);
    }
    return rows;
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
