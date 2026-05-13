import { Component, Input } from '@angular/core';
import { FeedbackTextQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_TEXT_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for text questions.
 */
@Component({
  selector: 'tm-text-question-additional-info',
  templateUrl: './text-question-additional-info.component.html',
})
export class TextQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackTextQuestionDetails = DEFAULT_TEXT_QUESTION_DETAILS();
}
