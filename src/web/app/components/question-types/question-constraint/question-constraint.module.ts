import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionConstraintComponent } from './contribution-question-constraint.component';
import { TextQuestionConstraintComponent } from './text-question-constraint.component';

/**
 * Module for all different types of question constraints.
 */
@NgModule({
  declarations: [
    ContributionQuestionConstraintComponent,
    TextQuestionConstraintComponent,
  ],
  exports: [
    ContributionQuestionConstraintComponent,
    TextQuestionConstraintComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionConstraintModule { }
