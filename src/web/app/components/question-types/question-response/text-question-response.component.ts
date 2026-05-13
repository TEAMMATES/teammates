import { Component, Input } from '@angular/core';
import { FeedbackTextResponseDetails } from '../../../../types/api-output';
import { DEFAULT_TEXT_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

/**
 * Text question response.
 */
@Component({
  selector: 'tm-text-question-response',
  templateUrl: './text-question-response.component.html',
})
export class TextQuestionResponseComponent {
  @Input() responseDetails: FeedbackTextResponseDetails = DEFAULT_TEXT_RESPONSE_DETAILS();
}
