import { Component } from '@angular/core';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS,
  DEFAULT_CONSTSUM_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The constsum question options submission form for a recipient.
 */
@Component({
  selector: 'tm-constsum-options-question-edit-answer-form',
  templateUrl: './constsum-options-question-edit-answer-form.component.html',
  styleUrls: ['./constsum-options-question-edit-answer-form.component.scss'],
})
export class ConstsumOptionsQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails> {

  // enum
  FeedbackConstantSumDistributePointsType: typeof FeedbackConstantSumDistributePointsType =
      FeedbackConstantSumDistributePointsType;

  constructor() {
    super(DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS(), DEFAULT_CONSTSUM_RESPONSE_DETAILS());
  }

  getAriaLabelForOption(option: String): String {
    const baseAriaLabel: String = this.getAriaLabel();
    return `${baseAriaLabel} for ${option} Option`;
  }

  /**
   * Assigns a point to the option specified by index.
   */
  triggerResponse(index: number, event: number): void {
    let newAnswers: number[] = this.responseDetails.answers.slice();

    if (newAnswers.length !== this.questionDetails.constSumOptions.length) {
      // initialize answers array on the fly
      newAnswers = Array(this.questionDetails.constSumOptions.length).fill(0);
    }

    newAnswers[index] = event ? Math.ceil(event) : 0;
    this.triggerResponseDetailsChange('answers', newAnswers);
  }

  /**
   * Gets total required points.
   */
  get totalRequiredPoints(): number {
    if (this.questionDetails.pointsPerOption) {
      return this.questionDetails.points * this.questionDetails.constSumOptions.length;
    }
    return this.questionDetails.points;
  }

  /**
   * Gets total answer points.
   */
  get totalAnsweredPoints(): number {
    return this.responseDetails.answers.reduce((total: number, curr: number) => total + curr, 0);
  }

  /**
   * Checks if all points are distributed unevenly.
   */
  get isAllPointsUneven(): boolean {
    const set: Set<number> = new Set();
    this.responseDetails.answers.forEach((ans: number) => set.add(ans));

    return set.size === this.responseDetails.answers.length;
  }

  /**
   * Checks if some points are distributed unevenly.
   */
  get isSomePointsUneven(): boolean {
    if (this.responseDetails.answers.length === 1) {
      return true;
    }

    const set: Set<number> = new Set();
    this.responseDetails.answers.forEach((ans: number) => set.add(ans));

    return set.size !== 1;
  }

  /**
   * Checks if any of the points are negative.
   */
  get isAnyPointsNegative(): boolean {
    return this.responseDetails.answers.reduce((isNegative: boolean, curr: number) => isNegative || (curr < 0), false);
  }

  /**
   * Checks if any of the points are below the minPoint.
   */
  get isAnyPointBelowMinimum(): boolean {
    const comparator : number = this.questionDetails.minPoint ? this.questionDetails.minPoint : 0;
    return this.responseDetails.answers.reduce((isBelowMinimum: boolean, curr: number) =>
      isBelowMinimum || (curr < comparator), false);
  }

  /**
   * Checks if any of the points are above the maxPoint.
   */
  get isAnyPointAboveMaximum(): boolean {
    const comparator : number = this.questionDetails.maxPoint
      ? this.questionDetails.maxPoint
      : this.totalRequiredPoints;
    return this.responseDetails.answers.reduce((isAboveMaximum: boolean, curr: number) =>
      isAboveMaximum || (curr > comparator), false);
  }

}
