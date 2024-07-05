import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountRequestTableComponent } from './account-request-table.component';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import {
  RejectWithReasonModalComponent,
} from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { Pipes } from '../../pipes/pipes.module';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';

/**
 * Module for account requests table.
 */
@NgModule({
  declarations: [
    AccountRequestTableComponent,
    EditRequestModalComponent,
    RejectWithReasonModalComponent,
  ],
  exports: [
    AccountRequestTableComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    NgbDropdownModule,
    Pipes,
    RichTextEditorModule,
    AjaxLoadingModule,
  ],
})
export class AccountRequestTableModule { }
