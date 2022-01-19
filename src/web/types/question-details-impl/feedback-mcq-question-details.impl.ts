// tslint:disable-next-line:max-line-length
import { McqQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/mcq-question-statistics-calculation';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackMcqMsqQuestionDetails } from './abstract-feedback-mcq-msq-question-details';

/**
 * Concrete implementation of {@link FeedbackMcqQuestionDetails}.
 */
export class FeedbackMcqQuestionDetailsImpl extends AbstractFeedbackMcqMsqQuestionDetails
    implements FeedbackMcqQuestionDetails {

  hasAssignedWeights: boolean = false;
  mcqWeights: number[] = [];
  mcqOtherWeight: number = 0;
  mcqChoices: string[] = [];
  otherEnabled: boolean = false;
  generateOptionsFor: FeedbackParticipantType = FeedbackParticipantType.NONE;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;

  constructor(apiOutput: FeedbackMcqQuestionDetails) {
    super();
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.mcqWeights = apiOutput.mcqWeights;
    this.mcqOtherWeight = apiOutput.mcqOtherWeight;
    this.mcqChoices = apiOutput.mcqChoices;
    this.otherEnabled = apiOutput.otherEnabled;
    this.generateOptionsFor = apiOutput.generateOptionsFor;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const statsCalculation: McqQuestionStatisticsCalculation = new McqQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

    statsRows.push(...this.getQuestionCsvStatsFrom(statsCalculation, statsCalculation.question.hasAssignedWeights));

    return statsRows;
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return true;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
