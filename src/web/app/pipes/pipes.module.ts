import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ResponseStatusPipe } from './session-response-status.pipe';
import { SubmissionStatusPipe } from './session-submission-status.pipe';

/**
 * Module for common pipes.
 */
@NgModule({
  declarations: [
    ResponseStatusPipe,
    SubmissionStatusPipe,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    ResponseStatusPipe,
    SubmissionStatusPipe,
  ],
})

export class Pipes {}
