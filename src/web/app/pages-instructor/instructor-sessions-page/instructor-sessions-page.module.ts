import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { RouterModule, Routes } from '@angular/router';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import {
  SessionsRecycleBinTableModule,
} from '../../components/sessions-recycle-bin-table/sessions-recycle-bin-table.module';
import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import {
  CopyFromOtherSessionsModalComponent,
} from './copy-from-other-sessions-modal/copy-from-other-sessions-modal.component';
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorSessionsPageComponent,
  },
];

/**
 * Module for instructor sessions page.
 */
@NgModule({
  imports: [
    CommonModule,
    SessionEditFormModule,
    TeammatesCommonModule,
    FormsModule,
    SessionsTableModule,
    SessionsRecycleBinTableModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    LoadingRetryModule,
    TeammatesRouterModule,
  ],
  declarations: [
    InstructorSessionsPageComponent,
    CopyFromOtherSessionsModalComponent,
  ],
  exports: [
    InstructorSessionsPageComponent,
  ],
  entryComponents: [
    CopyFromOtherSessionsModalComponent,
  ],
})
export class InstructorSessionsPageModule { }
