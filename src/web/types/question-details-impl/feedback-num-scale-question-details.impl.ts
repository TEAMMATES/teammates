import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackQuestionType, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackNumericalScaleQuestionDetails}.
 */
export class FeedbackNumericalScaleQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackNumericalScaleQuestionDetails {

  minScale: number = 1;
  maxScale: number = 5;
  step: number = 0.5;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.NUMSCALE;

  constructor(apiOutput: FeedbackNumericalScaleQuestionDetails) {
    super();
    this.minScale = apiOutput.minScale;
    this.maxScale = apiOutput.maxScale;
    this.step = apiOutput.step;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(_: QuestionOutput): string[][] {
    // TODO
    return [];
  }

}
