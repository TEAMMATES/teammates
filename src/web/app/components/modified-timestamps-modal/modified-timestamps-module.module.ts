import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ModifiedTimestampModalComponent } from './modified-timestamps-modal.component';

/**
 * Module for instructor home page.
 */
@NgModule({
  imports: [
    CommonModule,
    ModifiedTimestampModalComponent,
  ],
  exports: [
    ModifiedTimestampModalComponent,
  ],
})
export class ModifiedTimestampModalModule { }
