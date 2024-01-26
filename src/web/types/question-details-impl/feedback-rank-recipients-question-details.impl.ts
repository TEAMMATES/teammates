import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  RankRecipientsQuestionStatisticsCalculation,
} from '../../app/components/question-types/question-statistics/question-statistics-calculation/rank-recipients-question-statistics-calculation';
import {
  FeedbackQuestionType,
  FeedbackRankRecipientsQuestionDetails, QuestionOutput,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';

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

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];
    const emptyStr: string = '-';

    const statsCalculation: RankRecipientsQuestionStatisticsCalculation =
        new RankRecipientsQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

    statsRows.push([
      'Team',
      'Recipient',
      'Recipient Email',
      'Self Rank',
      'Overall Rank',
      'Overall Rank Excluding Self',
      'Team Rank',
      'Team Rank Excluding Self',
      'Ranks Received',
    ]);

    Object.keys(statsCalculation.ranksReceivedPerOption).sort().forEach((recipient: string) => {
      statsRows.push([
        statsCalculation.emailToTeamName[recipient],
        statsCalculation.emailToName[recipient],
        recipient,
        statsCalculation.selfRankPerOption[recipient]
            ? String(statsCalculation.selfRankPerOption[recipient]) : emptyStr,
        statsCalculation.rankPerOption[recipient] ? String(statsCalculation.rankPerOption[recipient]) : emptyStr,
        statsCalculation.rankPerOptionExcludeSelf[recipient]
            ? String(statsCalculation.rankPerOptionExcludeSelf[recipient]) : emptyStr,
        statsCalculation.rankPerOptionInTeam[recipient]
            ? String(statsCalculation.rankPerOptionInTeam[recipient]) : emptyStr,
        statsCalculation.rankPerOptionInTeamExcludeSelf[recipient]
            ? String(statsCalculation.rankPerOptionInTeamExcludeSelf[recipient]) : emptyStr,
        ...statsCalculation.ranksReceivedPerOption[recipient].map(String),
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
