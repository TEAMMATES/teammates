import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { ProgressBarModule } from '../progress-bar/progress-bar.module';
import { SimpleModalComponent } from './simple-modal.component';

/**
 * Module for the modal component
 */
@NgModule({
  declarations: [SimpleModalComponent],
  exports: [SimpleModalComponent],
  imports: [
    CommonModule,
    ProgressBarModule,
    AjaxLoadingModule,
  ],
  entryComponents: [SimpleModalComponent],
})

export class SimpleModalModule { }
