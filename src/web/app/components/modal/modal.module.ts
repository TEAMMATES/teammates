import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ModalComponent } from './modal.component';

/**
 * Module for the modal component
 */
@NgModule({
  declarations: [ModalComponent],
  exports: [ModalComponent],
  imports: [
    CommonModule,
  ],
  entryComponents: [ModalComponent],
})

export class ModalModule { }
