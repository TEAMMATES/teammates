import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';
import { ProgressBarComponent } from './progress-bar.component';

/**
 * Module for progress bar used to show download progress.
 */
@NgModule({
  declarations: [ProgressBarComponent],
  imports: [
    NgbProgressbarModule,
    CommonModule,
  ],
  exports: [
    ProgressBarComponent,
  ],
})
export class ProgressBarModule { }
