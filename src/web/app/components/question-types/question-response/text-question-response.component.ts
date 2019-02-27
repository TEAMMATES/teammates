import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackTextResponseDetails,
} from '../../../../types/api-output';
import { QuestionResponse } from './question-response';

/**
 * Text question response.
 */
@Component({
  selector: 'tm-text-question-response',
  templateUrl: './text-question-response.component.html',
  styleUrls: ['./text-question-response.component.scss'],
})
export class TextQuestionResponseComponent extends QuestionResponse<FeedbackTextResponseDetails> {

  constructor() {
    super({
      answer: '',
      questionType: FeedbackQuestionType.TEXT,
    });
  }

}
