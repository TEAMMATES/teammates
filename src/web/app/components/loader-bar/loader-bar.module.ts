import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatProgressBarModule } from '@angular/material';
import { LoaderBarComponent } from './loader-bar.component';

/**
 * Loading progress bar module.
 */
@NgModule({
  declarations: [LoaderBarComponent],
  imports: [
    MatProgressBarModule,
    CommonModule,
  ],
  exports: [
    LoaderBarComponent,
  ],
})
export class LoaderBarModule { }
