import { Component } from '@angular/core';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for contribution question.
 */
@Component({
  selector: 'tm-contribution-question-edit-details-form',
  templateUrl: './contribution-question-edit-details-form.component.html',
  styleUrls: ['./contribution-question-edit-details-form.component.scss'],
})
export class ContributionQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackContributionQuestionDetails> {

  constructor() {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

}
