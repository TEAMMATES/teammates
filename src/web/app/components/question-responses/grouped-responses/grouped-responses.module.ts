import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
// tslint:disable-next-line:max-line-length
import { ResponseModerationButtonModule } from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { SingleResponseModule } from '../single-response/single-response.module';
import { GroupedResponsesComponent } from './grouped-responses.component';

/**
 * Module for a list of responses grouped in GRQ/RGQ mode.
 */
@NgModule({
  declarations: [GroupedResponsesComponent],
  exports: [GroupedResponsesComponent],
  imports: [
    CommonModule,
    QuestionTextWithInfoModule,
    SingleResponseModule,
    ResponseModerationButtonModule,
    CommentBoxModule,
    TeammatesCommonModule,
    NgbTooltipModule,
  ],
})
export class GroupedResponsesModule { }
