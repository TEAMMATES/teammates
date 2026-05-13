import { AbstractFeedbackMcqMsqQuestionDetails } from './abstract-feedback-mcq-msq-question-details';
import {
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  QuestionOutput,
} from '../api-output';
import { Response } from '../question-statistics.model';
import { calculateMcqQuestionStatistics } from '../../app/utils/question-statistics.util';

/**
 * Concrete implementation of {@link FeedbackMcqQuestionDetails}.
 */
export class FeedbackMcqQuestionDetailsImpl
  extends AbstractFeedbackMcqMsqQuestionDetails
  implements FeedbackMcqQuestionDetails
{
  hasAssignedWeights = false;
  mcqWeights: number[] = [];
  mcqOtherWeight = 0;
  mcqChoices: string[] = [];
  otherEnabled = false;
  questionDropdownEnabled = false;
  generateOptionsFor: FeedbackParticipantType = FeedbackParticipantType.NONE;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;

  constructor(apiOutput: FeedbackMcqQuestionDetails) {
    super();
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.mcqWeights = apiOutput.mcqWeights;
    this.mcqOtherWeight = apiOutput.mcqOtherWeight;
    this.mcqChoices = apiOutput.mcqChoices;
    this.otherEnabled = apiOutput.otherEnabled;
    this.questionDropdownEnabled = apiOutput.questionDropdownEnabled;
    this.generateOptionsFor = apiOutput.generateOptionsFor;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];
    const questionDetails = question.feedbackQuestion.questionDetails as FeedbackMcqQuestionDetails;
    const responses = question.allResponses
      // Missing response is meaningless for statistics
      .filter((response) => !response.isMissingResponse) as unknown as Response<FeedbackMcqResponseDetails>[];

    if (responses.length === 0) {
      // skip stats for no response
      return [];
    }

    const statsCalculation = calculateMcqQuestionStatistics(questionDetails, responses);

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
