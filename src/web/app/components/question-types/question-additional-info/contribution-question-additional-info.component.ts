import { Component, Input } from '@angular/core';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-additional-info',
  templateUrl: './contribution-question-additional-info.component.html',
})
export class ContributionQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackContributionQuestionDetails = DEFAULT_CONTRIBUTION_QUESTION_DETAILS();
}
