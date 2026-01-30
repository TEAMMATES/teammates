import { Component } from '@angular/core';
import { QuestionResponse } from './question-response';
import {
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_MCQ_QUESTION_DETAILS,
  DEFAULT_MCQ_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { NgIf } from '@angular/common';
import { SafeHtmlPipe } from '../../teammates-common/safe-html.pipe';

/**
 * MCQ question response.
 */
@Component({
  selector: 'tm-mcq-question-response',
  templateUrl: './mcq-question-response.component.html',
  styleUrls: ['./mcq-question-response.component.scss'],
  imports: [NgIf, SafeHtmlPipe],
})
export class McqQuestionResponseComponent
    extends QuestionResponse<FeedbackMcqResponseDetails, FeedbackMcqQuestionDetails> {

  constructor() {
    super(DEFAULT_MCQ_RESPONSE_DETAILS(), DEFAULT_MCQ_QUESTION_DETAILS());
  }

}
