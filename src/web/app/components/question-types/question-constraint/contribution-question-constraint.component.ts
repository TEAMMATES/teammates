import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import {
  FeedbackResponseRecipientSubmissionFormModel,
} from '../../question-submission-form/question-submission-form-model';

/**
 * Constraint of contribution question.
 */
@Component({
  selector: 'tm-contribution-question-constraint',
  templateUrl: './contribution-question-constraint.component.html',
  styleUrls: ['./contribution-question-constraint.component.scss'],
})
export class ContributionQuestionConstraintComponent
  extends QuestionConstraintComponent<FeedbackContributionQuestionDetails> {

  constructor() {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

  get isAllFormsAnswered(): boolean {
    return this.recipientSubmissionForms.every((form: FeedbackResponseRecipientSubmissionFormModel) => {
      const details: FeedbackContributionResponseDetails = form.responseDetails as FeedbackContributionResponseDetails;
      return details.answer !== CONTRIBUTION_POINT_NOT_SUBMITTED;
    });
  }

  /**
   * Checks if all forms are not answered.
   */
  get isAllFormsNotAnswered(): boolean {
    return this.recipientSubmissionForms.every((form: FeedbackResponseRecipientSubmissionFormModel) => {
      const details: FeedbackContributionResponseDetails = form.responseDetails as FeedbackContributionResponseDetails;
      return details.answer === CONTRIBUTION_POINT_NOT_SUBMITTED;
    });
  }

  /**
   * Gets total required shares.
   */
  get totalRequiredContributions(): number {
    return 100 * this.recipientSubmissionForms.length;
  }

  /**
   * Gets all answers for the recipients.
   */
  get allAnswers(): number[] {
    return this.recipientSubmissionForms.map((form: FeedbackResponseRecipientSubmissionFormModel) => {
      const details: FeedbackContributionResponseDetails = form.responseDetails as FeedbackContributionResponseDetails;
      if (details.answer === CONTRIBUTION_POINT_NOT_SUBMITTED) {
        return 0;
      }
      return details.answer;
    });
  }

  /**
   * Gets total answer shares.
   */
  get totalAnsweredContributions(): number {
    return this.allAnswers.reduce((total: number, curr: number) => total + curr, 0);
  }

  /**
   * Checks if all shares have been distributed.
   */
  get isAllContributionsDistributed(): boolean {
    return this.totalAnsweredContributions === this.totalRequiredContributions;
  }

  /**
   * Checks if the shares have been insufficiently distributed.
   */
  get isInsufficientContributionsDistributed(): boolean {
    return this.totalAnsweredContributions < this.totalRequiredContributions;
  }

  /**
   * Checks if the shares have been over allocated.
   */
  get isContributionsOverAllocated(): boolean {
    return this.totalAnsweredContributions > this.totalRequiredContributions;
  }

  get isValid(): boolean {
    return !this.questionDetails.isZeroSum || !this.questionDetails.isNotSureAllowed
      || this.isAllFormsNotAnswered
      || (this.isAllFormsAnswered && this.isAllContributionsDistributed);
  }

  get currentTotalString(): string {
    if (this.totalAnsweredContributions === 0) {
      return '0%';
    }

    if (this.totalAnsweredContributions / 100 < 1) {
      return `1 x Equal Share - ${100 - this.totalAnsweredContributions % 100}%`;
    }

    const membersCount = this.totalRequiredContributions / 100;

    if (this.totalAnsweredContributions % 100 === 0 && this.totalAnsweredContributions / 100 <= membersCount) {
      return `${Math.floor(this.totalAnsweredContributions / 100)} x Equal Share`;
    }

    if (this.totalAnsweredContributions > this.totalRequiredContributions) {
      const excess = this.totalAnsweredContributions - this.totalRequiredContributions;

      return `${membersCount} x Equal Share + ${excess}%`;
    }

    return `${Math.floor(this.totalAnsweredContributions / 100)} x Equal Share +  
        ${this.totalAnsweredContributions % 100}%`;
  }

  get expectedTotalString(): string {
    return `${this.totalRequiredContributions / 100} x Equal Share`;
  }

}
