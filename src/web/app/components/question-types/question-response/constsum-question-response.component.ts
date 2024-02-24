import { Component, OnInit } from '@angular/core';
import { QuestionResponse } from './question-response';
import { FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS,
  DEFAULT_CONSTSUM_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * Constant sum question response.
 */
@Component({
  selector: 'tm-constsum-question-response',
  templateUrl: './constsum-question-response.component.html',
  styleUrls: ['./constsum-question-response.component.scss'],
})
export class ConstsumQuestionResponseComponent
    extends QuestionResponse<FeedbackConstantSumResponseDetails, FeedbackConstantSumQuestionDetails>
    implements OnInit {

  optionToAnswer: Record<string, number> = {};

  constructor() {
    super(DEFAULT_CONSTSUM_RESPONSE_DETAILS(), DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    for (let i: number = 0; i < this.questionDetails.constSumOptions.length; i += 1) {
      this.optionToAnswer[this.questionDetails.constSumOptions[i]] = this.responseDetails.answers[i];
    }
  }

}
