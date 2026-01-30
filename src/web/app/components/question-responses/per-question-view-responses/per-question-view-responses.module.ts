import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PerQuestionViewResponsesComponent } from './per-question-view-responses.component';

import { CommentBoxModule } from '../../comment-box/comment-box.module';



/**
 * Module for component to display list of responses for one question.
 */
@NgModule({
  exports: [
    PerQuestionViewResponsesComponent,
  ],
  providers: [
    NgbActiveModal,
  ],
  imports: [
    CommentBoxModule,
    CommonModule,
    RouterModule,
    PerQuestionViewResponsesComponent,
],
})
export class PerQuestionViewResponsesModule { }
