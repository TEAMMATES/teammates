import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { SimpleModalComponent } from './simple-modal.component';

/**
 * Module for the modal component
 */
@NgModule({
  declarations: [SimpleModalComponent],
  exports: [SimpleModalComponent],
  imports: [
    CommonModule,
    AjaxLoadingModule,
  ],
  entryComponents: [SimpleModalComponent],
})

export class SimpleModalModule { }
