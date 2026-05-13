import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  QuestionOutput,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { Response } from '../question-statistics.model';
import { calculateRankOptionsQuestionStatistics } from '../../app/utils/question-statistics.util';

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
    const statsRows: string[][] = [];

    const questionDetails = question.feedbackQuestion.questionDetails as FeedbackRankOptionsQuestionDetails;
    const responses = question.allResponses
      // Missing response is meaningless for statistics
      .filter((response) => !response.isMissingResponse) as unknown as Response<FeedbackRankOptionsResponseDetails>[];

    if (responses.length === 0) {
      // skip stats for no response
      return [];
    }
    const statsCalculation = calculateRankOptionsQuestionStatistics(questionDetails, responses);

    statsRows.push(['Option', 'Overall Rank', 'Ranks Received']);

    Object.keys(statsCalculation.ranksReceivedPerOption)
      .sort()
      .forEach((option: string) => {
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
