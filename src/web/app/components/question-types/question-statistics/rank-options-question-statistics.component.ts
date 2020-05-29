import { Component } from '@angular/core';
import {
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for rank options questions.
 */
@Component({
  selector: 'tm-rank-options-question-statistics',
  templateUrl: './rank-options-question-statistics.component.html',
  styleUrls: ['./rank-options-question-statistics.component.scss'],
})
export class RankOptionsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails> {

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
  }

}
