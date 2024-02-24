import { Component } from '@angular/core';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import {
  FeedbackRankRecipientsQuestionDetails,
} from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';

/**
 * Question details edit form component for Rank Recipients question.
 */
@Component({
  selector: 'tm-rank-recipients-question-edit-details-form',
  templateUrl: './rank-recipients-question-edit-details-form.component.html',
  styleUrls: ['./rank-recipients-question-edit-details-form.component.scss'],
})
export class RankRecipientsQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackRankRecipientsQuestionDetails> {

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS());
  }

  /**
   * Assigns a default value to minOptionsToBeRanked when checkbox is clicked.
   */
  triggerMinOptionsToBeRankedChange(checked: boolean): void {
    const minOptionsToBeRanked: number = checked ? 1 : NO_VALUE;
    this.triggerModelChange('minOptionsToBeRanked', minOptionsToBeRanked);
  }

  /**
   * Assigns a default value to maxOptionsToBeRanked when checkbox is clicked.
   */
  triggerMaxOptionsToBeRankedChange(checked: boolean): void {
    if (!checked) {
      this.triggerModelChange('maxOptionsToBeRanked', NO_VALUE);
      return;
    }

    if (this.isMinOptionsToBeRankedEnabled) {
      this.triggerModelChange('maxOptionsToBeRanked', this.model.minOptionsToBeRanked);
    } else {
      this.triggerModelChange('maxOptionsToBeRanked', 1);
    }
  }

  /**
   * Checks if the minOptionsToBeRanked checkbox is enabled.
   */
  get isMinOptionsToBeRankedEnabled(): boolean {
    return this.model.minOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Checks if the maxOptionsToBeRanked checkbox is enabled.
   */
  get isMaxOptionsToBeRankedEnabled(): boolean {
    return this.model.maxOptionsToBeRanked !== NO_VALUE;
  }

  /**
   * Displays minOptionsToBeRanked value.
   */
  get displayValueForMinOptionsToBeRanked(): any {
    return this.isMinOptionsToBeRankedEnabled ? this.model.minOptionsToBeRanked : '';
  }

  /**
   * Displays minOptionsToBeRanked value.
   */
  get displayValueForMaxOptionsToBeRanked(): any {
    return this.isMaxOptionsToBeRankedEnabled ? this.model.maxOptionsToBeRanked : '';
  }

  /**
   * Returns the maximum possible value for minOptionsToBeRanked.
   */
  get maxMinOptionsValue(): number {
    return this.isMaxOptionsToBeRankedEnabled ? this.model.maxOptionsToBeRanked : Number.MAX_VALUE;
  }
}
