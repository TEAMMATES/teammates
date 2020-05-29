import { Component } from '@angular/core';
import {
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-statistics',
  templateUrl: './rank-recipients-question-statistics.component.html',
  styleUrls: ['./rank-recipients-question-statistics.component.scss'],
})
export class RankRecipientsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackRankRecipientsQuestionDetails, FeedbackRankRecipientsResponseDetails> {

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS());
  }

}
