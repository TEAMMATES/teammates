import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';
import { NumScaleQuestionEditDetailsFormComponent } from './num-scale-question-edit-details-form.component';
import { TextQuestionEditDetailsFormComponent } from './text-question-edit-details-form.component';

/**
 * Module for all different types of question edit details forms.
 */
@NgModule({
  declarations: [
    ContributionQuestionEditDetailsFormComponent,
    NumScaleQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
  ],
  exports: [
    ContributionQuestionEditDetailsFormComponent,
    NumScaleQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
  ],
})
export class QuestionEditDetailsFormModule { }
