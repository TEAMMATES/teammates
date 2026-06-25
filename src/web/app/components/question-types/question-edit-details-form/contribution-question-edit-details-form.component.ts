import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { QuestionsSectionQuestions } from '../../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions';
import { Sections } from '../../../pages-help/instructor-help-page/sections';
import { RouterLink } from '@angular/router';

/**
 * Question details edit form component for contribution question.
 */
@Component({
  selector: 'tm-contribution-question-edit-details-form',
  templateUrl: './contribution-question-edit-details-form.component.html',
  imports: [FormsModule, RouterLink, NgbTooltip, NgClass],
})
export class ContributionQuestionEditDetailsFormComponent extends QuestionEditDetailsFormComponent<FeedbackContributionQuestionDetails> {
  // enum
  QuestionsSectionQuestions!: typeof QuestionsSectionQuestions;
  Sections!: typeof Sections;

  constructor() {
    super();
    this.QuestionsSectionQuestions = QuestionsSectionQuestions;
    this.Sections = Sections;
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
