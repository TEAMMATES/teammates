import { Component } from '@angular/core';

import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_NUMSCALE_QUESTION_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The numerical scale question submission form for a recipient.
 */
@Component({
  selector: 'tm-num-scale-question-edit-answer-form',
  templateUrl: './num-scale-question-edit-answer-form.component.html',
  styleUrls: ['./num-scale-question-edit-answer-form.component.scss'],
})
export class NumScaleQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackNumericalScaleQuestionDetails,
        FeedbackNumericalScaleResponseDetails> {

  readonly NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED: number = NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED;

  constructor() {
    super(DEFAULT_NUMSCALE_QUESTION_DETAILS(), DEFAULT_NUMSCALE_RESPONSE_DETAILS());
  }

  get numberOfPossibleValues(): number {
    const minValue: number = this.questionDetails.minScale;
    const maxValue: number = this.questionDetails.maxScale;
    const increment: number = this.questionDetails.step;
    const num: number = (maxValue - minValue) / increment + 1;

    return Math.floor(parseFloat(num.toFixed(3)));
  }

  get possibleValues(): string {

    if (this.numberOfPossibleValues > 6) {
      return `[${this.questionDetails.minScale},
           ${(Math.round((this.questionDetails.minScale + this.questionDetails.step) * 1000) / 1000).toString()},
           ${(Math.round((this.questionDetails.minScale + 2 * this.questionDetails.step) * 1000) / 1000).toString()},
           ...,
           ${(Math.round((this.questionDetails.maxScale - 2 * this.questionDetails.step) * 1000) / 1000).toString()},
           ${(Math.round((this.questionDetails.maxScale - this.questionDetails.step) * 1000) / 1000).toString()},
           ${this.questionDetails.maxScale}]`;
    }
    let possibleValuesString: string = `[${this.questionDetails.minScale.toString()}`;
    let currentValue: number = this.questionDetails.minScale + this.questionDetails.step;

    while (this.questionDetails.maxScale - currentValue >= -1e-9) {
      possibleValuesString += `, ${(Math.round(currentValue * 1000) / 1000).toString()}`;
      currentValue += this.questionDetails.step;
    }
    return `${possibleValuesString}]`;
  }

  /**
   * Checks if the answer value is a valid value in question possible values.
   */
  isValidPossibleValue(value: number): boolean {
    const minValue: number = this.questionDetails.minScale;
    const maxValue: number = this.questionDetails.maxScale;
    const increment: number = this.questionDetails.step;

    if (isNaN(value)) {
      return false;
    }

    return value >= minValue && value <= maxValue && (value - minValue) % increment === 0;
  }

}
