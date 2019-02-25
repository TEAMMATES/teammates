import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';
import { TextQuestionEditDetailsFormComponent } from './text-question-edit-details-form.component';

/**
 * Module for all different types of question edit details forms.
 */
@NgModule({
  declarations: [
    ContributionQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
  ],
  exports: [
    ContributionQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
})
export class QuestionEditDetailsFormModule { }
