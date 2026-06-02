import { KeyValuePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackConstantSumOptionsResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS,
  DEFAULT_CONSTSUM_OPTIONS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * Constant sum options question response.
 */
@Component({
  selector: 'tm-constsum-options-question-response',
  templateUrl: './constsum-options-question-response.component.html',
  imports: [KeyValuePipe],
})
export class ConstsumOptionsQuestionResponseComponent implements OnInit {
  @Input() responseDetails: FeedbackConstantSumOptionsResponseDetails = DEFAULT_CONSTSUM_OPTIONS_RESPONSE_DETAILS();
  @Input() questionDetails: FeedbackConstantSumOptionsQuestionDetails = DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS();

  optionToAnswer: Record<string, number> = {};

  ngOnInit(): void {
    for (let i = 0; i < this.questionDetails.constSumOptions.length; i += 1) {
      this.optionToAnswer[this.questionDetails.constSumOptions[i]] = this.responseDetails.answers[i];
    }
  }
}
