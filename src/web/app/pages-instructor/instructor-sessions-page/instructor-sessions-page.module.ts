import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import {
  CopyFromOtherSessionsModalComponent,
} from './copy-from-other-sessions-modal/copy-from-other-sessions-modal.component';
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';
import { RecycleBinTableFormatDatePipe } from './recycle-bin-table-format-date.pipe';
import {
  SessionPermanentDeletionConfirmModalComponent,
} from './session-permanent-deletion-confirm-modal/session-permanent-deletion-confirm-modal.component';
import {
  SessionsPermanentDeletionConfirmModalComponent,
} from './sessions-permanent-deletion-confirm-modal/sessions-permanent-deletion-confirm-modal.component';

/**
 * Module for instructor sessions page.
 */
@NgModule({
  imports: [
    CommonModule,
    SessionEditFormModule,
    TeammatesCommonModule,
    NgbModule,
    FormsModule,
    SessionsTableModule,
  ],
  declarations: [
    InstructorSessionsPageComponent,
    CopyFromOtherSessionsModalComponent,
    SessionPermanentDeletionConfirmModalComponent,
    SessionsPermanentDeletionConfirmModalComponent,
    RecycleBinTableFormatDatePipe,
  ],
  exports: [
    InstructorSessionsPageComponent,
  ],
  entryComponents: [
    CopyFromOtherSessionsModalComponent,
    SessionPermanentDeletionConfirmModalComponent,
    SessionsPermanentDeletionConfirmModalComponent,
  ],
})
export class InstructorSessionsPageModule { }
