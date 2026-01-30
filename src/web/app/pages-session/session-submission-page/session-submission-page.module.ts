import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { InViewportModule } from 'ng-in-viewport';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';
import { SessionSubmissionPageComponent } from './session-submission-page.component';



import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';


const routes: Routes = [
  {
    path: '',
    component: SessionSubmissionPageComponent,
  },
];

/**
 * Module for feedback session submission page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbDropdownModule,
    NgbTooltipModule,
    QuestionSubmissionFormModule,
    RouterModule.forChild(routes),
    InViewportModule,
    SavingCompleteModalComponent,
    SessionSubmissionPageComponent,
],
  exports: [
    SessionSubmissionPageComponent,
  ],
})
export class SessionSubmissionPageModule { }
