import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CopySessionModalComponent } from './copy-session-modal.component';

/**
 * Module for copy current session modal.
 */
@NgModule({
  declarations: [
    CopySessionModalComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
  exports: [
    CopySessionModalComponent,
  ],
})
export class CopySessionModalModule { }
