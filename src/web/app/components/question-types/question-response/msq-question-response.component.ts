import { Component } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_MSQ_QUESTION_DETAILS,
  DEFAULT_MSQ_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { QuestionResponse } from './question-response';

/**
 * MSQ question response.
 */
@Component({
  selector: 'tm-msq-question-response',
  templateUrl: './msq-question-response.component.html',
  styleUrls: ['./msq-question-response.component.scss'],
})
export class MsqQuestionResponseComponent
    extends QuestionResponse<FeedbackMsqResponseDetails, FeedbackMsqQuestionDetails> {

  constructor() {
    super(DEFAULT_MSQ_RESPONSE_DETAILS(), DEFAULT_MSQ_QUESTION_DETAILS());
  }

}
