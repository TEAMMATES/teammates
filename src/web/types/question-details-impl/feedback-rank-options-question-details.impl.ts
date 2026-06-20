import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import { FeedbackQuestionType, FeedbackRankOptionsQuestionDetails, QuestionOutput } from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

/**
 * Concrete implementation of {@link FeedbackRankOptionsQuestionDetails}.
 */
export class FeedbackRankOptionsQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackRankOptionsQuestionDetails
{
  minOptionsToBeRanked: number = NO_VALUE;
  maxOptionsToBeRanked: number = NO_VALUE;
  areDuplicatesAllowed = false;
  options: string[] = [];
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.RANK_OPTIONS;

  constructor(apiOutput: FeedbackRankOptionsQuestionDetails) {
    super();
    this.minOptionsToBeRanked = apiOutput.minOptionsToBeRanked;
    this.maxOptionsToBeRanked = apiOutput.maxOptionsToBeRanked;
    this.areDuplicatesAllowed = apiOutput.areDuplicatesAllowed;
    this.options = apiOutput.options;
    this.questionText = apiOutput.questionText;
  }

  override getQuestionCsvHeaders(): string[] {
    const optionsHeader: string[] = this.options.map((_: string, index: number) => `Rank ${index + 1}`);
    return ['Feedback', ...optionsHeader];
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isRankOptions(stats) || stats.options.length === 0) {
      return [];
    }

    const rows: string[][] = [['Option', 'Overall Rank', 'Ranks Received']];
    for (const row of stats.options) {
      rows.push([row.option, row.overallRank != null ? String(row.overallRank) : '', ...row.ranksReceived.map(String)]);
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
