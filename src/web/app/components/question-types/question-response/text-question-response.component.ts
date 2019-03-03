import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
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
export class TextQuestionResponseComponent
    extends QuestionResponse<FeedbackTextResponseDetails, FeedbackTextQuestionDetails> {

  constructor() {
    super({
      answer: '',
      questionType: FeedbackQuestionType.TEXT,
    }, {
      recommendedLength: 0,
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    });
  }

}
