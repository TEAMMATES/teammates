import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ProgressBarModule } from '../../components/progress-bar/progress-bar.module';
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
  ],
  entryComponents: [SimpleModalComponent],
})

export class SimpleModalModule { }
