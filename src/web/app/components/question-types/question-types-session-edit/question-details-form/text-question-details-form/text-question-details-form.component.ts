import { Component, OnInit } from '@angular/core';
import {
  FeedbackTextQuestionDetails,
} from '../../../../../feedback-question';
import { QuestionDetailsFormComponent } from '../question-details-form.component';

/**
 * Question details edit form component for text question.
 */
@Component({
  selector: 'tm-text-question-details-form',
  templateUrl: './text-question-details-form.component.html',
  styleUrls: ['./text-question-details-form.component.scss'],
})
export class TextQuestionDetailsFormComponent extends QuestionDetailsFormComponent<FeedbackTextQuestionDetails>
    implements OnInit {

  constructor() {
    super({
      recommendedLength: 0,
    });
  }

  ngOnInit(): void {
  }
}
