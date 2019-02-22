import { Component, OnInit } from '@angular/core';
import { FeedbackQuestionType, FeedbackTextQuestionDetails } from '../../../../types/api-output';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for text question.
 */
@Component({
  selector: 'tm-text-question-edit-details-form',
  templateUrl: './text-question-edit-details-form.component.html',
  styleUrls: ['./text-question-edit-details-form.component.scss'],
})
export class TextQuestionEditDetailsFormComponent extends QuestionEditDetailsFormComponent<FeedbackTextQuestionDetails>
    implements OnInit {

  constructor() {
    super({
      recommendedLength: 0,
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    });
  }

  ngOnInit(): void {
  }
}
