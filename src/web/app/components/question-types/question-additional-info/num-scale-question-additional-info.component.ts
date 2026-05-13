import { Component, Input } from '@angular/core';
import { FeedbackNumericalScaleQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-additional-info',
  templateUrl: './num-scale-question-additional-info.component.html',
})
export class NumScaleQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackNumericalScaleQuestionDetails = DEFAULT_NUMSCALE_QUESTION_DETAILS();
}
