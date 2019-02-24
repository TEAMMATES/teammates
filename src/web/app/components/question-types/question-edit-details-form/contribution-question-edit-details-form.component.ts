import { Component } from '@angular/core';
import { FeedbackContributionQuestionDetails, FeedbackQuestionType } from '../../../../types/api-output';
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
    super({
      isNotSureAllowed: false,
      questionText: '',
      questionType: FeedbackQuestionType.CONTRIB,
    });
  }

}
