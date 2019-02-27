import { Input, OnInit } from '@angular/core';
import { FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract question response.
 */
export abstract class QuestionResponse<R extends FeedbackResponseDetails> implements OnInit {

  @Input()
  responseDetails: R;

  protected constructor(responseDetails: R) {
    this.responseDetails = responseDetails;
  }

  ngOnInit(): void {
  }

}
