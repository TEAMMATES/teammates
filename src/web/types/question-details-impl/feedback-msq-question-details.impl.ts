import { AbstractFeedbackMcqMsqQuestionDetails } from './abstract-feedback-mcq-msq-question-details';
import { FeedbackMsqQuestionDetails, FeedbackQuestionType, QuestionOutput, QuestionRecipientType } from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

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
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isMcqMsqCourseWide(stats) || !stats.hasAnswers) {
      return [];
    }
    return this.getQuestionCsvStatsFrom(stats);
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return true;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
