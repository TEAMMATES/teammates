import { Component, Input, OnInit } from '@angular/core';
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
export class ConstsumRecipientsQuestionConstraintComponent implements OnInit {

  // enum
  FeedbackConstantSumDistributePointsType: typeof FeedbackConstantSumDistributePointsType =
      FeedbackConstantSumDistributePointsType;

  @Input()
  questionDetails: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS();

  @Input()
  recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [];

  constructor() { }

  ngOnInit(): void {
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
}
