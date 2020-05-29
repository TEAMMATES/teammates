import { Component } from '@angular/core';
import { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails } from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-statistics',
  templateUrl: './mcq-question-statistics.component.html',
  styleUrls: ['./mcq-question-statistics.component.scss'],
})
export class McqQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails> {

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS());
  }

}
