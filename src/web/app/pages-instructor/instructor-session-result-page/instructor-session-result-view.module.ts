import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbCollapseModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { GqrRqgViewResponsesModule } from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.module';
import { GrqRgqViewResponsesModule } from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import { PerQuestionViewResponsesModule } from '../../components/question-responses/per-question-view-responses/per-question-view-responses.module';
import { SingleStatisticsModule } from '../../components/question-responses/single-statistics/single-statistics.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';

/**
 * Module for different view components of instructor session results page.
 */
@NgModule({
  declarations: [
    InstructorSessionResultQuestionViewComponent,
    InstructorSessionResultRgqViewComponent,
    InstructorSessionResultGrqViewComponent,
    InstructorSessionResultRqgViewComponent,
    InstructorSessionResultGqrViewComponent,
    InstructorSessionNoResponsePanelComponent,
  ],
  exports: [
    InstructorSessionResultQuestionViewComponent,
    InstructorSessionResultRgqViewComponent,
    InstructorSessionResultGrqViewComponent,
    InstructorSessionResultRqgViewComponent,
    InstructorSessionResultGqrViewComponent,
    InstructorSessionNoResponsePanelComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbCollapseModule,
    NgbTooltipModule,
    RouterModule,
    QuestionTextWithInfoModule,
    PerQuestionViewResponsesModule,
    GqrRqgViewResponsesModule,
    GrqRgqViewResponsesModule,
    SingleStatisticsModule,
    LoadingSpinnerModule,
    PanelChevronModule,
    TeammatesRouterModule,
  ],
  providers: [
    CommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
})
export class InstructorSessionResultViewModule { }
