import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SessionSubmissionPageComponent } from './session-submission-page.component';

/**
 * Module for feedback session submission page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    SessionSubmissionPageComponent,
  ],
  exports: [
    SessionSubmissionPageComponent,
  ],
})
export class SessionSubmissionPageModule { }
