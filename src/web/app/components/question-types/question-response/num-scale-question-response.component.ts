import { Component, Input } from '@angular/core';
import { FeedbackNumericalScaleResponseDetails } from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

/**
 * Numerical scale question response.
 */
@Component({
  selector: 'tm-num-scale-question-response',
  templateUrl: './num-scale-question-response.component.html',
})
export class NumScaleQuestionResponseComponent {
  @Input() responseDetails: FeedbackNumericalScaleResponseDetails = DEFAULT_NUMSCALE_RESPONSE_DETAILS();
}
