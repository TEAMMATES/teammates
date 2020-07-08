import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConfirmationModalComponent } from './confirmation-modal.component';

/**
 * Module for the modal component
 */
@NgModule({
  declarations: [ConfirmationModalComponent],
  exports: [ConfirmationModalComponent],
  imports: [
    CommonModule,
  ],
  entryComponents: [ConfirmationModalComponent],
})

export class ConfirmationModalModule { }
