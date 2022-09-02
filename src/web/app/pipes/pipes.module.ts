import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SearchTermsHighlighterPipe } from './search-terms-highlighter.pipe';
import { ResponseStatusPipe } from './session-response-status.pipe';
import { SubmissionStatusPipe } from './session-submission-status.pipe';

/**
 * Module for common pipes.
 */
@NgModule({
  declarations: [
    ResponseStatusPipe,
    SubmissionStatusPipe,
    SearchTermsHighlighterPipe,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    ResponseStatusPipe,
    SubmissionStatusPipe,
    SearchTermsHighlighterPipe,
  ],
})

export class Pipes {}
