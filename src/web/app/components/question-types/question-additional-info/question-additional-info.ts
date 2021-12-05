import { Directive, Input, OnInit } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';

/**
 * The abstract question additional info section.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export abstract class QuestionAdditionalInfo<Q extends FeedbackQuestionDetails> implements OnInit {

  @Input()
  questionDetails: Q;

  protected constructor(questionDetails: Q) {
    this.questionDetails = questionDetails;
  }

  ngOnInit(): void {
  }

}
