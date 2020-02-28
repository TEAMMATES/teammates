import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StatusMessageModalComponent } from './status-message-modal.component';

/**
 * Module for status message modal
 */
@NgModule({
  declarations: [StatusMessageModalComponent],
  imports: [
    CommonModule,
  ],
  exports: [
    StatusMessageModalComponent,
  ],
  entryComponents: [
    StatusMessageModalComponent,
  ],
})
export class StatusMesssageModalModule { }
