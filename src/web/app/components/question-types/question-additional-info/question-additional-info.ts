import { Directive, Input } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';

/**
 * The abstract question additional info section.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class QuestionAdditionalInfo<Q extends FeedbackQuestionDetails> {

  @Input()
  questionDetails: Q;

  protected constructor(questionDetails: Q) {
    this.questionDetails = questionDetails;
  }

}
