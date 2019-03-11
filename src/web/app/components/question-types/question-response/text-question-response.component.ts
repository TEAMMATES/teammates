import { Component } from '@angular/core';
import {
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_TEXT_QUESTION_DETAILS,
  DEFAULT_TEXT_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
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
    super(DEFAULT_TEXT_RESPONSE_DETAILS(), DEFAULT_TEXT_QUESTION_DETAILS());
  }

}
