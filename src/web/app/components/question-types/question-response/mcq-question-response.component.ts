import { Component, Input } from '@angular/core';
import { FeedbackMcqResponseDetails } from '../../../../types/api-output';
import { DEFAULT_MCQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { SafeHtmlPipe } from '../../teammates-common/safe-html.pipe';

/**
 * MCQ question response.
 */
@Component({
  selector: 'tm-mcq-question-response',
  templateUrl: './mcq-question-response.component.html',
  imports: [SafeHtmlPipe],
})
export class McqQuestionResponseComponent {
  @Input() responseDetails: FeedbackMcqResponseDetails = DEFAULT_MCQ_RESPONSE_DETAILS();
}
