import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackRubricQuestionDetails}.
 */
export class FeedbackRubricQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackRubricQuestionDetails {

  hasAssignedWeights: boolean = false;
  numOfRubricChoices: number = 0;
  rubricChoices: string[] = [];
  numOfRubricSubQuestions: number = 0;
  rubricSubQuestions: string[] = [];
  rubricWeightsForEachCell: number[][] = [];
  rubricDescriptions: string[][] = [];
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.RUBRIC;

  constructor(apiOutput: FeedbackRubricQuestionDetails) {
    super();
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.numOfRubricChoices = apiOutput.numOfRubricChoices;
    this.rubricChoices = apiOutput.rubricChoices;
    this.numOfRubricSubQuestions = apiOutput.numOfRubricSubQuestions;
    this.rubricSubQuestions = apiOutput.rubricSubQuestions;
    this.rubricWeightsForEachCell = apiOutput.rubricWeightsForEachCell;
    this.rubricDescriptions = apiOutput.rubricDescriptions;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvHeaders(): string[] {
    return ['Sub Question', 'Choice Value', 'Choice Number'];
  }

  getMissingResponseCsvAnswers(): string[][] {
    return [['All Sub-Questions', 'No Response']];
  }

  getQuestionCsvStats(_: QuestionOutput): string[][] {
    // TODO
    return [];
  }
}
