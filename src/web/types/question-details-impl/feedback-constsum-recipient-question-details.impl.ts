import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackQuestionType,
  QuestionOutput,
} from '../api-output';
import { Response } from '../question-statistics.model';
import { calculateConstsumRecipientsQuestionStatistics } from '../../app/utils/question-statistics.util';

/**
 * Concrete implementation of {@link FeedbackConstantSumQuestionDetails}.
 */
export class FeedbackConstantSumRecipientsQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackConstantSumQuestionDetails
{
  constSumOptions: string[] = [];
  distributeToRecipients = true;
  pointsPerOption = false;
  forceUnevenDistribution = false;
  distributePointsFor: string = FeedbackConstantSumDistributePointsType.NONE;
  points = 100;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;
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

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];
    const responses = question.allResponses
      // Missing response is meaningless for statistics
      .filter((response) => !response.isMissingResponse) as unknown as Response<FeedbackConstantSumResponseDetails>[];
    const recipientType = question.feedbackQuestion.recipientType;

    if (responses.length === 0) {
      // skip stats for no response
      return [];
    }

    const statsCalculation = calculateConstsumRecipientsQuestionStatistics(responses, recipientType);

    statsRows.push(['Team', 'Recipient', 'Recipient Email', 'Total Points', 'Average Points', 'Points Received']);

    Object.keys(statsCalculation.pointsPerOption)
      .sort()
      .forEach((recipient: string) => {
        statsRows.push([
          statsCalculation.emailToTeamName[recipient],
          statsCalculation.emailToName[recipient],
          recipient,
          String(statsCalculation.totalPointsPerOption[recipient]),
          String(statsCalculation.averagePointsPerOption[recipient]),
          ...statsCalculation.pointsPerOption[recipient].map(String),
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
