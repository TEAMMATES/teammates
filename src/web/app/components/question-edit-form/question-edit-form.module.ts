import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbCollapseModule, NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { QuestionEditFormComponent } from './question-edit-form.component';



import {
  QuestionEditBriefDescriptionFormModule,
} from '../question-edit-brief-description-form/question-edit-brief-description-form.module';





/**
 * Module for all question edit UI in session edit page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbCollapseModule,
    NgbDropdownModule,
    NgbTooltipModule,
    QuestionEditBriefDescriptionFormModule,
    QuestionEditFormComponent,
],
  exports: [
    QuestionEditFormComponent,
  ],
})
export class QuestionEditFormModule { }
