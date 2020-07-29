// tslint:disable-next-line:max-line-length
import { RankOptionsQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/rank-options-question-statistics-calculation';
import {
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails, QuestionOutput,
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

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const statsCalculation: RankOptionsQuestionStatisticsCalculation
        = new RankOptionsQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

    statsRows.push(['Option', 'Overall Rank', 'Ranks Received']);

    Object.keys(statsCalculation.ranksReceivedPerOption).sort().forEach((option: string) => {
      statsRows.push([
        option,
        statsCalculation.rankPerOption[option] ? String(statsCalculation.rankPerOption[option]) : '',
        ...statsCalculation.ranksReceivedPerOption[option].map(String),
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
