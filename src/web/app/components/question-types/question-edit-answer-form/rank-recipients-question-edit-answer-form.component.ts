import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';
import { FeedbackRankRecipientsResponseDetails } from '../../../../types/api-output';
import { RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';

/**
 * The Rank recipients question submission form for a recipient.
 */
@Component({
  selector: 'tm-rank-recipients-question-edit-answer-form',
  templateUrl: './rank-recipients-question-edit-answer-form.component.html',
  styleUrls: ['./rank-recipients-question-edit-answer-form.component.scss'],
  imports: [FormsModule],
})
export class RankRecipientsQuestionEditAnswerFormComponent extends QuestionEditAnswerFormComponent<FeedbackRankRecipientsResponseDetails> {
  readonly RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED: number = RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED;

  @Input()
  numOfRecipients = 0;

  /**
   * Populates the possible Ranks that can be assigned.
   */
  get ranksToBeAssigned(): number[] {
    const ranks: number[] = [];
    for (let i = 1; i <= this.numOfRecipients; i += 1) {
      ranks.push(i);
    }
    return ranks;
  }
}
