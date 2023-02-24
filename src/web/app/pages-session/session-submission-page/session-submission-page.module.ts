import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { InViewportModule } from 'ng-in-viewport';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';
import { SessionSubmissionPageComponent } from './session-submission-page.component';

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
    AjaxLoadingModule,
    TeammatesCommonModule,
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    QuestionSubmissionFormModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    LoadingRetryModule,
    InViewportModule,
  ],
  declarations: [
    SavingCompleteModalComponent,
    SessionSubmissionPageComponent,
  ],
  exports: [
    SessionSubmissionPageComponent,
  ],
})
export class SessionSubmissionPageModule { }
