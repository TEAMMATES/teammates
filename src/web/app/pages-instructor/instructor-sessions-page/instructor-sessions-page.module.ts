import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { RouterModule, Routes } from '@angular/router';
import {
  CopyFromOtherSessionsModalComponent,
} from './copy-from-other-sessions-modal/copy-from-other-sessions-modal.component';
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';
import {
  SessionPermanentDeletionConfirmModalComponent,
} from './session-permanent-deletion-confirm-modal/session-permanent-deletion-confirm-modal.component';
import {
  SessionsPermanentDeletionConfirmModalComponent,
} from './sessions-permanent-deletion-confirm-modal/sessions-permanent-deletion-confirm-modal.component';



import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';

import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';



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
  exports: [InstructorSessionsPageComponent],
  imports: [
    CommonModule,
    SessionEditFormModule,
    FormsModule,
    SessionsTableModule,
    RouterModule.forChild(routes),
    InstructorSessionsPageComponent,
    CopyFromOtherSessionsModalComponent,
    SessionPermanentDeletionConfirmModalComponent,
    SessionsPermanentDeletionConfirmModalComponent,
],
})
export class InstructorSessionsPageModule { }
