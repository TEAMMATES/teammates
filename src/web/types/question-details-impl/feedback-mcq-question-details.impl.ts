import { AbstractFeedbackMcqMsqQuestionDetails } from './abstract-feedback-mcq-msq-question-details';
import { FeedbackMcqQuestionDetails, FeedbackQuestionType, QuestionOutput, QuestionRecipientType } from '../api-output';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

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
  generateOptionsFor: QuestionRecipientType = QuestionRecipientType.NONE;
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
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isMcqMsqCourseWide(stats) || stats.rows.length === 0) {
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
