import { AbstractFeedbackMcqMsqQuestionDetails } from './abstract-feedback-mcq-msq-question-details';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackQuestionType,
  QuestionOutput,
  QuestionRecipientType,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { Response } from '../question-statistics.model';
import { calculateMsqQuestionStatistics } from '../../app/utils/question-statistics.util';

/**
 * Concrete implementation of {@link FeedbackMsqQuestionDetails}.
 */
export class FeedbackMsqQuestionDetailsImpl
  extends AbstractFeedbackMcqMsqQuestionDetails
  implements FeedbackMsqQuestionDetails
{
  msqChoices: string[] = [];
  otherEnabled = false;
  generateOptionsFor: QuestionRecipientType = QuestionRecipientType.NONE;
  maxSelectableChoices: number = NO_VALUE;
  minSelectableChoices: number = NO_VALUE;
  hasAssignedWeights = false;
  msqWeights: number[] = [];
  msqOtherWeight = 0;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MSQ;

  constructor(apiOutput: FeedbackMsqQuestionDetails) {
    super();
    this.msqChoices = apiOutput.msqChoices;
    this.otherEnabled = apiOutput.otherEnabled;
    this.generateOptionsFor = apiOutput.generateOptionsFor;
    this.maxSelectableChoices = apiOutput.maxSelectableChoices;
    this.minSelectableChoices = apiOutput.minSelectableChoices;
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.msqWeights = apiOutput.msqWeights;
    this.msqOtherWeight = apiOutput.msqOtherWeight;
    this.questionText = apiOutput.questionText;
  }

  override getQuestionCsvHeaders(): string[] {
    return ['Feedback', ...this.msqChoices];
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const questionDetails = question.feedbackQuestion.questionDetails as FeedbackMsqQuestionDetails;
    const responses = question.allResponses
      // Missing response is meaningless for statistics
      .filter((response) => !response.isMissingResponse) as unknown as Response<FeedbackMsqResponseDetails>[];

    const statsCalculation = calculateMsqQuestionStatistics(questionDetails, responses);
    if (responses.length === 0 || !statsCalculation.hasAnswers) {
      // skip stats for no response
      return [];
    }

    statsRows.push(...this.getQuestionCsvStatsFrom(statsCalculation, questionDetails.hasAssignedWeights));

    return statsRows;
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return true;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
