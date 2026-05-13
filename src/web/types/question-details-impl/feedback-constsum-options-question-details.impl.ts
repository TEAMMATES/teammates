import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackQuestionType,
  QuestionOutput,
} from '../api-output';
import { calculateConstsumOptionsQuestionStatistics } from '../../app/utils/question-statistics.util';
import { Response } from '../question-statistics.model';

/**
 * Concrete implementation of {@link FeedbackConstantSumQuestionDetails}.
 */
export class FeedbackConstantSumOptionsQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackConstantSumQuestionDetails
{
  constSumOptions: string[] = ['', ''];
  distributeToRecipients = false;
  pointsPerOption = false;
  forceUnevenDistribution = false;
  distributePointsFor: string = FeedbackConstantSumDistributePointsType.NONE;
  points = 100;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
  minPoint: number | undefined = undefined;
  maxPoint: number | undefined = undefined;

  constructor(apiOutput: FeedbackConstantSumQuestionDetails) {
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
    const statsRows: string[][] = [];
    const questionDetails = question.feedbackQuestion.questionDetails as FeedbackConstantSumQuestionDetails;
    const responses = question.allResponses
      // Missing response is meaningless for statistics
      .filter((response) => !response.isMissingResponse) as unknown as Response<FeedbackConstantSumResponseDetails>[];

    if (responses.length === 0) {
      // skip stats for no response
      return [];
    }

    const statsCalculation = calculateConstsumOptionsQuestionStatistics(questionDetails, responses);

    statsRows.push(['Option', 'Total Points', 'Average Points', 'Points Received']);

    Object.keys(statsCalculation.pointsPerOption)
      .sort()
      .forEach((option: string) => {
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
