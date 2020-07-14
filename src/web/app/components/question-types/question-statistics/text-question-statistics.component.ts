import { Component } from '@angular/core';
import { FeedbackTextQuestionDetails, FeedbackTextResponseDetails } from '../../../../types/api-output';
import { DEFAULT_TEXT_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for text questions.
 */
@Component({
  selector: 'tm-text-question-statistics',
  templateUrl: './text-question-statistics.component.html',
  styleUrls: ['./text-question-statistics.component.scss'],
})
export class TextQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackTextQuestionDetails, FeedbackTextResponseDetails> {

  constructor() {
    super(DEFAULT_TEXT_QUESTION_DETAILS());
  }

}
