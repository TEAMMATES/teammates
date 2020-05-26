import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import {
  NO_VALUE,
  RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED,
} from '../../../../types/feedback-response-details';
import {
  FeedbackResponseRecipientSubmissionFormModel,
} from '../../question-submission-form/question-submission-form-model';

/**
 * Constraint of rank recipients question.
 */
@Component({
  selector: 'tm-rank-recipients-question-constraint',
  templateUrl: './rank-recipients-question-constraint.component.html',
  styleUrls: ['./rank-recipients-question-constraint.component.scss'],
})
export class RankRecipientsQuestionConstraintComponent implements OnInit {

  @Input()
  questionDetails: FeedbackRankRecipientsQuestionDetails = DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS();

  @Input()
  recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Checks if the answer has same Ranks for different recipients.
   */
  get isSameRanksAssigned(): boolean {
    const allRanks: Set<number> = new Set();

    for (const submissionForm of this.recipientSubmissionForms) {
      const details: FeedbackRankRecipientsResponseDetails =
          submissionForm.responseDetails as FeedbackRankRecipientsResponseDetails;
      if (details.answer === RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED) {
        // ignore not submitted
        continue;
      }

      if (allRanks.has(details.answer)) {
        return true;
      }
      allRanks.add(details.answer);
    }
    return false;
  }

  /**
   * Checks if no recipient has been ranked.
   */
  get isNoRecipientRanked(): boolean {
    for (const submissionForm of this.recipientSubmissionForms) {
      const details: FeedbackRankRecipientsResponseDetails =
          submissionForm.responseDetails as FeedbackRankRecipientsResponseDetails;
      if (details.answer !== RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks if a minimum number of recipients needs to be Ranked.
   */
  get isMinRecipientsEnabled(): boolean {
    return this.questionDetails.minOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Checks if a maximum number of recipients can be Ranked.
   */
  get isMaxRecipientsEnabled(): boolean {
    return this.questionDetails.maxOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Checks if the recipients Ranked is less than the minimum required.
   */
  get isRecipientsRankedLessThanMin(): boolean {
    let numberOfRecipientsRanked: number = 0;
    for (const submissionForm of this.recipientSubmissionForms) {
      const details: FeedbackRankRecipientsResponseDetails =
          submissionForm.responseDetails as FeedbackRankRecipientsResponseDetails;
      if (details.answer !== RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED) {
        numberOfRecipientsRanked += 1;
      }
    }

    return (numberOfRecipientsRanked < this.questionDetails.minOptionsToBeRanked && numberOfRecipientsRanked > 0);
  }

  /**
   * Checks if the recipients Ranked is more than the maximum required.
   */
  get isRecipientsRankedMoreThanMax(): boolean {
    let numberOfRecipientsRanked: number = 0;
    for (const submissionForm of this.recipientSubmissionForms) {
      const details: FeedbackRankRecipientsResponseDetails =
          submissionForm.responseDetails as FeedbackRankRecipientsResponseDetails;
      if (details.answer !== RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED) {
        numberOfRecipientsRanked += 1;
      }
    }

    return numberOfRecipientsRanked > this.questionDetails.maxOptionsToBeRanked;
  }

}
