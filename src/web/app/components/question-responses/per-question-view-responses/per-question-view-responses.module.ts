import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PerQuestionViewResponsesComponent } from './per-question-view-responses.component';
import {
  ResponseModerationButtonModule,
} from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { SingleResponseModule } from '../single-response/single-response.module';

/**
 * Module for component to display list of responses for one question.
 */
@NgModule({
  declarations: [
    PerQuestionViewResponsesComponent,
  ],
  exports: [
    PerQuestionViewResponsesComponent,
  ],
  providers: [
    NgbActiveModal,
  ],
  imports: [
    CommentBoxModule,
    CommonModule,
    TeammatesCommonModule,
    RouterModule,
    SingleResponseModule,
    ResponseModerationButtonModule,
  ],
})
export class PerQuestionViewResponsesModule { }
