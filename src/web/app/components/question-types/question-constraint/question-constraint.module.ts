import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionConstraintComponent } from './contribution-question-constraint.component';
import { MsqQuestionConstraintComponent } from './msq-question-constraint.component';
import { NumScaleQuestionConstraintComponent } from './num-scale-question-constraint.component';
import { RankRecipientsQuestionConstraintComponent } from './rank-recipients-question-constraint.component';
import { TextQuestionConstraintComponent } from './text-question-constraint.component';

/**
 * Module for all different types of question constraints.
 */
@NgModule({
  declarations: [
    ContributionQuestionConstraintComponent,
    NumScaleQuestionConstraintComponent,
    TextQuestionConstraintComponent,
    MsqQuestionConstraintComponent,
    RankRecipientsQuestionConstraintComponent,
  ],
  exports: [
    ContributionQuestionConstraintComponent,
    NumScaleQuestionConstraintComponent,
    TextQuestionConstraintComponent,
    MsqQuestionConstraintComponent,
    RankRecipientsQuestionConstraintComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionConstraintModule { }
