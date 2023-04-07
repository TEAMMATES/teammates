import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  NgbDropdownModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { AjaxPreloadModule } from '../ajax-preload/ajax-preload.module';
import { CopySessionModalModule } from '../copy-session-modal/copy-session-modal.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { PublishStatusTooltipPipe } from './publish-status-tooltip.pipe';
import {
  ResendResultsLinkToRespondentModalComponent,
} from './resend-results-link-to-respondent-modal/resend-results-link-to-respondent-modal.component';
import { RespondentListInfoTableComponent } from './respondent-list-info-table/respondent-list-info-table.component';
import {
  SendRemindersToRespondentsModalComponent,
} from './send-reminders-to-respondents-modal/send-reminders-to-respondents-modal.component';
import { SessionsTableComponent } from './sessions-table.component';

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
    TeammatesCommonModule,
    TeammatesRouterModule,
  ],
  exports: [SessionsTableComponent, RespondentListInfoTableComponent],
})
export class SessionsTableModule {}
