import { Component } from '@angular/core';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import { FeedbackTextQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_TEXT_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Question details edit form component for text question.
 */
@Component({
  selector: 'tm-text-question-edit-details-form',
  templateUrl: './text-question-edit-details-form.component.html',
  styleUrls: ['./text-question-edit-details-form.component.scss'],
})
export class TextQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackTextQuestionDetails> {

  constructor() {
    super(DEFAULT_TEXT_QUESTION_DETAILS());
  }

}
