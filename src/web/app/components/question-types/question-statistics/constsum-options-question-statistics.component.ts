import { Component } from '@angular/core';
import { FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-statistics',
  templateUrl: './constsum-options-question-statistics.component.html',
  styleUrls: ['./constsum-options-question-statistics.component.scss'],
})
export class ConstsumOptionsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails> {

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS());
  }

}
