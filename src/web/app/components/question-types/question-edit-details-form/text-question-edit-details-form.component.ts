import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import { FeedbackTextQuestionDetails } from '../../../../types/api-output';

/**
 * Question details edit form component for text question.
 */
@Component({
  selector: 'tm-text-question-edit-details-form',
  templateUrl: './text-question-edit-details-form.component.html',
  styleUrls: ['./text-question-edit-details-form.component.scss'],
  imports: [NgbTooltip, FormsModule],
})
export class TextQuestionEditDetailsFormComponent extends QuestionEditDetailsFormComponent<FeedbackTextQuestionDetails> {}
