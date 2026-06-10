import { Component, Input } from '@angular/core';
import { QuestionRecipientType } from '../../../types/api-output';

/**
 * Warning shown when there are no valid recipients for a question.
 */
@Component({
  selector: 'tm-no-recipients-warning',
  templateUrl: './no-recipients-warning.component.html',
})
export class NoRecipientsWarningComponent {
  // enum
  QuestionRecipientType!: typeof QuestionRecipientType;

  @Input()
  recipientType: QuestionRecipientType = QuestionRecipientType.NONE;

  constructor() {
    this.QuestionRecipientType = QuestionRecipientType;
  }
}
