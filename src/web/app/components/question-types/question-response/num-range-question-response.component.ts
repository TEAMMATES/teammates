import { Component } from '@angular/core';
import { FeedbackNumericalRangeQuestionDetails, FeedbackNumericalRangeResponseDetails } from '../../../../types/api-output';
import { DEFAULT_NUMRANGE_QUESTION_DETAILS, DEFAULT_NUMRANGE_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { QuestionResponse } from './question-response';

@Component({
  selector: 'tm-num-range-question-response',
  templateUrl: './num-range-question-response.component.html',
  styleUrls: ['./num-range-question-response.component.scss']
})
export class NumRangeQuestionResponseComponent
    extends QuestionResponse<FeedbackNumericalRangeResponseDetails, FeedbackNumericalRangeQuestionDetails> {

  constructor() {
    super(DEFAULT_NUMRANGE_RESPONSE_DETAILS(), DEFAULT_NUMRANGE_QUESTION_DETAILS())
  }

}
