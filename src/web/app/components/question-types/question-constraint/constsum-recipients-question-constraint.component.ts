import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';
import {
  FeedbackResponseRecipientSubmissionFormModel,
} from '../../question-submission-form/question-submission-form-model';

/**
 * Constraint of constsum recipients question.
 */
@Component({
  selector: 'tm-constsum-recipients-question-constraint',
  templateUrl: './constsum-recipients-question-constraint.component.html',
  styleUrls: ['./constsum-recipients-question-constraint.component.scss'],
})
export class ConstsumRecipientsQuestionConstraintComponent
    extends QuestionConstraintComponent<FeedbackConstantSumQuestionDetails> {

  // enum
  FeedbackConstantSumDistributePointsType: typeof FeedbackConstantSumDistributePointsType =
      FeedbackConstantSumDistributePointsType;

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS());
  }

  /**
   * Checks if all forms are not answered.
   */
  get isAllFormsNotAnswered(): boolean {
    return this.recipientSubmissionForms.every((form: FeedbackResponseRecipientSubmissionFormModel) => {
      const details: FeedbackConstantSumResponseDetails = form.responseDetails as FeedbackConstantSumResponseDetails;
      return details.answers.length === 0;
    });
  }

  /**
   * Gets total required points.
   */
  get totalRequiredPoints(): number {
    if (this.questionDetails.pointsPerOption) {
      return this.questionDetails.points * this.recipientSubmissionForms.length;
    }
    return this.questionDetails.points;
  }

  /**
   * Get answers for the recipients.
   */
  get allAnswers(): number[] {
    return this.recipientSubmissionForms.map(
        (form: FeedbackResponseRecipientSubmissionFormModel) => {
          const details: FeedbackConstantSumResponseDetails =
              form.responseDetails as FeedbackConstantSumResponseDetails;
          if (details.answers.length === 0) {
            return 0;
          }
          return details.answers[0];
        });
  }

  /**
   * Gets total answer points.
   */
  get totalAnsweredPoints(): number {
    return this.allAnswers.reduce((total: number, curr: number) => total + curr, 0);
  }

  /**
   * Checks if all points are distributed unevenly.
   */
  get isAllPointsUneven(): boolean {
    const set: Set<number> = new Set();
    this.allAnswers.forEach((ans: number) => set.add(ans));

    return set.size === this.allAnswers.length;
  }

  /**
   * Checks if some points are distributed unevenly.
   */
  get isSomePointsUneven(): boolean {
    if (this.allAnswers.length === 1) {
      return true;
    }

    const set: Set<number> = new Set();
    this.allAnswers.forEach((ans: number) => set.add(ans));

    return set.size !== 1;
  }

  /**
   * Checks if all points have been distributed.
   */
  get isAllPointsDistributed(): boolean {
    return this.totalAnsweredPoints === this.totalRequiredPoints;
  }

  /**
   * Checks if the points have been insufficiently distributed.
   */
  get isInsufficientPointsDistributed(): boolean {
    return this.totalAnsweredPoints < this.totalRequiredPoints;
  }

  /**
   * Checks if the points have been over allocated.
   */
  get isPointsOverAllocated(): boolean {
    return this.totalAnsweredPoints > this.totalRequiredPoints;
  }

  /**
   * Checks if any of the points are negative.
   */
  get isAnyPointsNegative(): boolean {
    return this.allAnswers.reduce((isNegative: boolean, curr: number) => isNegative || (curr < 0), false);
  }

  /**
   * Returns true if the question requires uneven distribution but the points are not unevenly distributed.
   */
  get isWronglyAllUneven(): boolean {
    return this.questionDetails.distributePointsFor === FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY
        && !this.isAllPointsUneven;
  }

  /**
   * Returns true if the question requires uneven distribution and the points are unevenly distributed.
   */
  get isCorrectlyAllUneven(): boolean {
    return this.questionDetails.distributePointsFor === FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY
        && this.isAllPointsUneven;
  }

  /**
   * Returns true if the question requires some uneven distribution but points are not unevenly distributed for some.
   */
  get isWronglySomeUneven(): boolean {
    return this.questionDetails.distributePointsFor === FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY
        && !this.isSomePointsUneven;
  }

  /**
   * Returns true if the question requires some uneven distribution and points are unevenly distributed for some.
   */
  get isCorrectlySomeUneven(): boolean {
    return this.questionDetails.distributePointsFor === FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY
        && this.isSomePointsUneven;
  }

  get isValid(): boolean {
    return this.isAllPointsDistributed && !this.isAnyPointsNegative
        && (this.isCorrectlyAllUneven || this.isCorrectlySomeUneven
        || this.questionDetails.distributePointsFor === FeedbackConstantSumDistributePointsType.NONE);
  }
}
