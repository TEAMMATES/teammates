import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SimpleModalComponent } from './simple-modal.component';

/**
 * Module for the modal component
 */
@NgModule({
  declarations: [SimpleModalComponent],
  exports: [SimpleModalComponent],
  imports: [
    CommonModule,
  ],
  entryComponents: [SimpleModalComponent],
})

export class SimpleModalModule { }
