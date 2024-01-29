import { Component } from '@angular/core';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import { FeedbackNumericalScaleQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Question details edit form component for numerical scale question.
 */
@Component({
  selector: 'tm-num-scale-question-edit-details-form',
  templateUrl: './num-scale-question-edit-details-form.component.html',
  styleUrls: ['./num-scale-question-edit-details-form.component.scss'],
})
export class NumScaleQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackNumericalScaleQuestionDetails> {

  Math: typeof Math = Math;

  constructor() {
    super(DEFAULT_NUMSCALE_QUESTION_DETAILS());
  }

  /**
   * Returns the number of possible values acceptable as answers.
   */
  get numberOfPossibleValues(): number {
    const minValue: number = this.model.minScale;
    const maxValue: number = this.model.maxScale;
    const increment: number = this.model.step;
    const num: number = (maxValue - minValue) / increment + 1;

    return Math.floor(parseFloat(num.toFixed(3)));
  }

  /**
   * Returns the possible answers for the given max and min values.
   */
  get possibleValues(): string {

    if (this.numberOfPossibleValues > 6) {
      const maxAcceptableValue: number =
          this.model.minScale + ((this.numberOfPossibleValues - 1) * this.model.step);
      return `[${this.model.minScale},
           ${+(this.model.minScale + this.model.step).toFixed(3)},
           ${+(this.model.minScale + 2 * this.model.step).toFixed(3)}, ...,
           ${+(maxAcceptableValue - 2 * this.model.step).toFixed(3)},
           ${+(maxAcceptableValue - this.model.step).toFixed(3)},
           ${+maxAcceptableValue.toFixed(3)}]`;
    }
    let possibleValuesString: string = `${this.model.minScale}`;
    let currentValue: number = this.model.minScale + this.model.step;

    while (this.model.maxScale - currentValue >= -1e-9) {
      possibleValuesString += `, ${+currentValue.toFixed(3)}`;
      currentValue += this.model.step;
    }
    return `[${possibleValuesString}]`;
  }
}
