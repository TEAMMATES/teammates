import { Component, Input } from '@angular/core';
import { FeedbackMsqResponseDetails } from '../../../../types/api-output';
import { DEFAULT_MSQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { MSQ_ANSWER_NONE_OF_THE_ABOVE } from '../../../../types/feedback-response-details';

/**
 * MSQ question response.
 */
@Component({
  selector: 'tm-msq-question-response',
  templateUrl: './msq-question-response.component.html',
  imports: [],
})
export class MsqQuestionResponseComponent {
  @Input() responseDetails: FeedbackMsqResponseDetails = DEFAULT_MSQ_RESPONSE_DETAILS();

  /**
   * Checks whether the MSQ answer is 'None of the above'.
   */
  get isNoneOfTheAboveAnswer(): boolean {
    return (
      this.responseDetails.answers.length === 1 && this.responseDetails.answers[0] === MSQ_ANSWER_NONE_OF_THE_ABOVE
    );
  }
}
