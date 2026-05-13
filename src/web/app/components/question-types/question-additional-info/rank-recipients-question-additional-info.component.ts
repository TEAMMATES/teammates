import { Component, Input } from '@angular/core';
import { FeedbackRankRecipientsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-additional-info',
  templateUrl: './rank-recipients-question-additional-info.component.html',
})
export class RankRecipientsQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackRankRecipientsQuestionDetails = DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS();
}
