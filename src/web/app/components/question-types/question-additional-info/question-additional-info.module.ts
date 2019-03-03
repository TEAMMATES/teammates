import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionAdditionalInfoComponent } from './contribution-question-additional-info.component';
import { NumScaleQuestionAdditionalInfoComponent } from './num-scale-question-additional-info.component';
import { TextQuestionAdditionalInfoComponent } from './text-question-additional-info.component';

/**
 * Module for all additional info components for all different question types.
 */
@NgModule({
  declarations: [
    ContributionQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
  ],
  exports: [
    ContributionQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionAdditionalInfoModule { }
