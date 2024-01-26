import { Component } from '@angular/core';
import { QuestionAdditionalInfo } from './question-additional-info';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-additional-info',
  templateUrl: './contribution-question-additional-info.component.html',
  styleUrls: ['./contribution-question-additional-info.component.scss'],
})
export class ContributionQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackContributionQuestionDetails> {

  constructor() {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

}
