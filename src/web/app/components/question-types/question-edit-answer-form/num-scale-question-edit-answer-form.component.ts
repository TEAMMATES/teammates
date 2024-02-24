import { Component } from '@angular/core';

import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_NUMSCALE_QUESTION_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';

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
      const maxAcceptableValue: number =
          this.questionDetails.minScale + ((this.numberOfPossibleValues - 1) * this.questionDetails.step);
      return `[${this.questionDetails.minScale},
           ${+(this.questionDetails.minScale + this.questionDetails.step).toFixed(3)},
           ${+(this.questionDetails.minScale + 2 * this.questionDetails.step).toFixed(3)},
           ...,
           ${+(maxAcceptableValue - 2 * this.questionDetails.step).toFixed(3)},
           ${+(maxAcceptableValue - this.questionDetails.step).toFixed(3)},
           ${+maxAcceptableValue.toFixed(3)}]`;
    }
    let possibleValuesString: string = `${this.questionDetails.minScale}`;
    let currentValue: number = this.questionDetails.minScale + this.questionDetails.step;

    while (this.questionDetails.maxScale - currentValue >= -1e-9) {
      possibleValuesString += `, ${+currentValue.toFixed(3)}`;
      currentValue += this.questionDetails.step;
    }
    return `[${possibleValuesString}]`;
  }

  /**
   * Checks if the answer value is a valid value in question possible values.
   */
  isValidPossibleValue(value: number): boolean {
    const minValue: number = this.questionDetails.minScale;
    const maxValue: number = this.questionDetails.maxScale;
    const increment: number = this.questionDetails.step;

    if (value == null) {
      return true;
    }

    if (Number.isNaN(value)) {
      return false;
    }

    if (value < minValue || value > maxValue) {
      return false;
    }

    return +((value - minValue) / increment).toFixed(6) % 1 === 0;
  }

}
