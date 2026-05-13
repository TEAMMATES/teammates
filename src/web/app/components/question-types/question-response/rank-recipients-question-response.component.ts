import { Component, Input } from '@angular/core';
import { FeedbackRankRecipientsResponseDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

/**
 * Rank recipients question response.
 */
@Component({
  selector: 'tm-rank-recipients-question-response',
  templateUrl: './rank-recipients-question-response.component.html',
})
export class RankRecipientsQuestionResponseComponent {
  @Input() responseDetails: FeedbackRankRecipientsResponseDetails = DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS();
}
