import { Component, Input } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for rank options questions.
 */
@Component({
  selector: 'tm-rank-options-question-additional-info',
  templateUrl: './rank-options-question-additional-info.component.html',
  imports: [],
})
export class RankOptionsQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackRankOptionsQuestionDetails = DEFAULT_RANK_OPTIONS_QUESTION_DETAILS();
}
