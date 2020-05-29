import { Component } from '@angular/core';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-statistics',
  templateUrl: './contribution-question-statistics.component.html',
  styleUrls: ['./contribution-question-statistics.component.scss'],
})
export class ContributionQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails> {

  constructor() {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

}
