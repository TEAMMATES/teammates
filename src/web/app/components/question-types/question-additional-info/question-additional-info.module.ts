import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionAdditionalInfoComponent } from './contribution-question-additional-info.component';
import { TextQuestionAdditionalInfoComponent } from './text-question-additional-info.component';

/**
 * Module for all additional info components for all different question types.
 */
@NgModule({
  declarations: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
  ],
  exports: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionAdditionalInfoModule { }
