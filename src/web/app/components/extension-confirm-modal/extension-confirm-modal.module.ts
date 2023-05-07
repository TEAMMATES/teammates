import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { ExtensionConfirmModalComponent } from './extension-confirm-modal.component';
import { SortableTableModule } from '../../components/sortable-table/sortable-table.module';

/**
 * Module for confirming deadline extensions.
 */
@NgModule({
  declarations: [
    ExtensionConfirmModalComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    TeammatesCommonModule,
    SortableTableModule,
  ],
  exports: [
    ExtensionConfirmModalComponent,
  ],
})
export class ExtensionConfirmModalModule { }
