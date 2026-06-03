import { Component, Input } from '@angular/core';
import { FeedbackConstantSumRecipientsResponseDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_RECIPIENTS_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

/**
 * Constant sum recipients question response.
 */
@Component({
  selector: 'tm-constsum-recipients-question-response',
  templateUrl: './constsum-recipients-question-response.component.html',
})
export class ConstsumRecipientsQuestionResponseComponent {
  @Input() responseDetails: FeedbackConstantSumRecipientsResponseDetails =
    DEFAULT_CONSTSUM_RECIPIENTS_RESPONSE_DETAILS();
}
