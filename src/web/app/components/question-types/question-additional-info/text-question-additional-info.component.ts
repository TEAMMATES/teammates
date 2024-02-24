import { Component } from '@angular/core';
import { QuestionAdditionalInfo } from './question-additional-info';
import { FeedbackTextQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_TEXT_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for text questions.
 */
@Component({
  selector: 'tm-text-question-additional-info',
  templateUrl: './text-question-additional-info.component.html',
  styleUrls: ['./text-question-additional-info.component.scss'],
})
export class TextQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackTextQuestionDetails> {

  constructor() {
    super(DEFAULT_TEXT_QUESTION_DETAILS());
  }

}
