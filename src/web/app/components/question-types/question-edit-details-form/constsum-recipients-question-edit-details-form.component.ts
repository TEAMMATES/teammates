import { Component } from '@angular/core';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * Question details edit form component for constsum recipients question.
 */
@Component({
  selector: 'tm-constsum-recipients-question-edit-details-form',
  templateUrl: './constsum-recipients-question-edit-details-form.component.html',
  styleUrls: ['./constsum-recipients-question-edit-details-form.component.scss'],
})
export class ConstsumRecipientsQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackConstantSumQuestionDetails> {

  // enum
  FeedbackConstantSumDistributePointsType: typeof FeedbackConstantSumDistributePointsType =
      FeedbackConstantSumDistributePointsType;

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS());
  }

  /**
   * Changes force uneven distribution option.
   */
  onForceUnevenDistribution(event: boolean): void {
    this.triggerModelChangeBatch({
      forceUnevenDistribution: event,
      distributePointsFor: event ? FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY
          : FeedbackConstantSumDistributePointsType.NONE,
    });
  }
}
