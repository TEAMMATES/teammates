import { Component } from '@angular/core';
import { FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails } from '../../../../types/api-output';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-statistics',
  templateUrl: './rubric-question-statistics.component.html',
  styleUrls: ['./rubric-question-statistics.component.scss'],
})
export class RubricQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails> {

  constructor() {
    super(DEFAULT_RUBRIC_QUESTION_DETAILS());
  }

}
