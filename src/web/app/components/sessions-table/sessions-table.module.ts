import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  NgbDropdownModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { PublishStatusTooltipPipe } from './publish-status-tooltip.pipe';
import {
  ResendResultsLinkToRespondentModalComponent,
} from './resend-results-link-to-respondent-modal/resend-results-link-to-respondent-modal.component';
import { RespondentListInfoTableComponent } from './respondent-list-info-table/respondent-list-info-table.component';
import {
  SendRemindersToRespondentsModalComponent,
} from './send-reminders-to-respondents-modal/send-reminders-to-respondents-modal.component';
import { SessionsTableComponent } from './sessions-table.component';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { AjaxPreloadModule } from '../ajax-preload/ajax-preload.module';
import { CopySessionModalModule } from '../copy-session-modal/copy-session-modal.module';
import { SortableTableModule } from '../sortable-table/sortable-table.module';
import { FormatDateBriefPipe } from '../teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../teammates-common/format-date-detail.pipe';
import { PublishStatusNamePipe } from '../teammates-common/publish-status-name.pipe';
import { SubmissionStatusNamePipe } from '../teammates-common/submission-status-name.pipe';
import { SubmissionStatusTooltipPipe } from '../teammates-common/submission-status-tooltip.pipe';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

/**
 * Module for sessions table.
 */
@NgModule({
  declarations: [
    PublishStatusTooltipPipe,
    SessionsTableComponent,
    ResendResultsLinkToRespondentModalComponent,
    SendRemindersToRespondentsModalComponent,
    RespondentListInfoTableComponent,
  ],
  exports: [SessionsTableComponent, RespondentListInfoTableComponent],
  imports: [
    CommonModule,
    AjaxLoadingModule,
    AjaxPreloadModule,
    TeammatesCommonModule,
    NgbDropdownModule,
    NgbTooltipModule,
    FormsModule,
    CopySessionModalModule,
    RouterModule,
    TeammatesRouterModule,
    SortableTableModule,
  ],
  providers: [
    FormatDateDetailPipe,
    FormatDateBriefPipe,
    PublishStatusNamePipe,
    PublishStatusTooltipPipe,
    SubmissionStatusNamePipe,
    SubmissionStatusTooltipPipe,
  ],
})
export class SessionsTableModule {}
