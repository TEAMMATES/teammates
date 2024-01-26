import { AbstractFeedbackMcqMsqQuestionDetails } from './abstract-feedback-mcq-msq-question-details';
import {
  MsqQuestionStatisticsCalculation,
} from '../../app/components/question-types/question-statistics/question-statistics-calculation/msq-question-statistics-calculation';
import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType, QuestionOutput,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackMsqQuestionDetails}.
 */
export class FeedbackMsqQuestionDetailsImpl extends AbstractFeedbackMcqMsqQuestionDetails
    implements FeedbackMsqQuestionDetails {

  msqChoices: string[] = [];
  otherEnabled: boolean = false;
  generateOptionsFor: FeedbackParticipantType = FeedbackParticipantType.NONE;
  maxSelectableChoices: number = NO_VALUE;
  minSelectableChoices: number = NO_VALUE;
  hasAssignedWeights: boolean = false;
  msqWeights: number[] = [];
  msqOtherWeight: number = 0;
  questionText: string = '';
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

    const statsCalculation: MsqQuestionStatisticsCalculation = new MsqQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    statsCalculation.calculateStatistics();
    if (statsCalculation.responses.length === 0 || !statsCalculation.hasAnswers) {
      // skip stats for no response
      return [];
    }

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
