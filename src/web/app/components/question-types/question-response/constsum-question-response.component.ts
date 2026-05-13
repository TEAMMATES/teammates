import { KeyValuePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
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
  imports: [KeyValuePipe],
})
export class ConstsumQuestionResponseComponent implements OnInit {
  @Input() responseDetails: FeedbackConstantSumResponseDetails = DEFAULT_CONSTSUM_RESPONSE_DETAILS();
  @Input() questionDetails: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS();

  optionToAnswer: Record<string, number> = {};

  ngOnInit(): void {
    for (let i = 0; i < this.questionDetails.constSumOptions.length; i += 1) {
      this.optionToAnswer[this.questionDetails.constSumOptions[i]] = this.responseDetails.answers[i];
    }
  }
}
