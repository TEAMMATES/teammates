import { Component } from '@angular/core';
import { FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails } from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-statistics',
  templateUrl: './msq-question-statistics.component.html',
  styleUrls: ['./msq-question-statistics.component.scss'],
})
export class MsqQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails> {

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS());
  }

}
