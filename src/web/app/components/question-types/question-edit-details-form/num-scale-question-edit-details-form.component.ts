import { Component } from '@angular/core';
import { FeedbackNumericalScaleQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

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

  constructor() {
    super(DEFAULT_NUMSCALE_QUESTION_DETAILS());
  }

  get maxScaleValue(): number {
    if (this.model.maxScale <= this.model.minScale) {
      this.model.maxScale = this.model.minScale + this.model.step;
    }
    return this.model.maxScale;
  }

  get isIntervalDivisible(): boolean {
    if (this.model.step <= 0) {
      return false;
    }
    const largestValueInRange: number = this.model.minScale + (this.numberOfPossibleValues - 1) * this.model.step;
    return largestValueInRange === this.model.maxScale;
  }

  get numberOfPossibleValues(): number {
    const minValue: number = this.model.minScale;
    const maxValue: number = this.model.maxScale;
    const increment: number = this.model.step;
    const num: number = (maxValue - minValue) / increment + 1;

    return Math.floor(parseFloat(num.toFixed(3)));
  }

  get possibleValues(): string {

    if (this.numberOfPossibleValues > 6) {
      return `[${this.model.minScale},
           ${(Math.round((this.model.minScale + this.model.step) * 1000) / 1000).toString()},
           ${(Math.round((this.model.minScale + 2 * this.model.step) * 1000) / 1000).toString()}, ...,
           ${(Math.round((this.model.maxScale - 2 * this.model.step) * 1000) / 1000).toString()},
           ${(Math.round((this.model.maxScale - this.model.step) * 1000) / 1000).toString()},
           ${this.model.maxScale}]`;
    }
    let possibleValuesString: string = `[${this.model.minScale.toString()}`;
    let currentValue: number = this.model.minScale + this.model.step;

    while (this.model.maxScale - currentValue >= -1e-9) {
      possibleValuesString += `, ${(Math.round(currentValue * 1000) / 1000).toString()}`;
      currentValue += this.model.step;
    }
    return `${possibleValuesString}]`;
  }
}
