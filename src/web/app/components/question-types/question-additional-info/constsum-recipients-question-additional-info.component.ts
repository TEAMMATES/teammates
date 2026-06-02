import { Component, Input } from '@angular/core';
import { FeedbackConstantSumRecipientsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for constsum recipients questions.
 */
@Component({
  selector: 'tm-constsum-recipients-question-additional-info',
  templateUrl: './constsum-recipients-question-additional-info.component.html',
  imports: [],
})
export class ConstsumRecipientsQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackConstantSumRecipientsQuestionDetails =
    DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS();
}
