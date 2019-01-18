import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import {
  QuestionTypesSessionSubmissionModule,
} from '../../components/question-types/question-types-session-submission/question-types-session-submission.module';
import {
  FeedbackSessionClosedModalComponent,
  FeedbackSessionClosingSoonModalComponent, FeedbackSessionDeletedModalComponent,
  FeedbackSessionNotOpenModalComponent, SavingCompleteModalComponent,
} from './session-submission-modals.component';
import { SessionSubmissionPageComponent } from './session-submission-page.component';

/**
 * Module for feedback session submission page.
 */
@NgModule({
  imports: [
    AjaxLoadingModule,
    CommonModule,
    FormsModule,
    NgbModule,
    QuestionTypesSessionSubmissionModule,
  ],
  declarations: [
    SavingCompleteModalComponent,
    SessionSubmissionPageComponent,
    FeedbackSessionClosingSoonModalComponent,
    FeedbackSessionClosedModalComponent,
    FeedbackSessionNotOpenModalComponent,
    FeedbackSessionDeletedModalComponent,
  ],
  exports: [
    SessionSubmissionPageComponent,
  ],
  entryComponents: [
    SavingCompleteModalComponent,
    FeedbackSessionClosingSoonModalComponent,
    FeedbackSessionClosedModalComponent,
    FeedbackSessionNotOpenModalComponent,
    FeedbackSessionDeletedModalComponent,
  ],
})
export class SessionSubmissionPageModule { }
