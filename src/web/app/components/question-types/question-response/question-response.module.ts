import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionResponseComponent } from './contribution-question-response.component';
import { TextQuestionResponseComponent } from './text-question-response.component';

/**
 * Module for all different types of question responses.
 */
@NgModule({
  declarations: [
    ContributionQuestionResponseComponent,
    TextQuestionResponseComponent,
  ],
  exports: [
    ContributionQuestionResponseComponent,
    TextQuestionResponseComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionResponseModule { }
