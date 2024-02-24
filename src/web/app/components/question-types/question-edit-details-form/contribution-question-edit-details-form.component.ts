import { Component } from '@angular/core';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import {
  QuestionsSectionQuestions,
} from '../../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions';
import { Sections } from '../../../pages-help/instructor-help-page/sections';

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

  // enum
  QuestionsSectionQuestions: typeof QuestionsSectionQuestions = QuestionsSectionQuestions;
  Sections: typeof Sections = Sections;

  constructor() {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

  /**
   * Triggers the change of the zero sum checkbox.
   */
  triggerModelChangeForIsZeroSum(zeroSum: boolean): void {
    if (zeroSum) {
      this.triggerModelChangeBatch({
        isZeroSum: zeroSum,
        isNotSureAllowed: false,
      });
    } else {
      this.triggerModelChange('isZeroSum', zeroSum);
    }
  }
}
