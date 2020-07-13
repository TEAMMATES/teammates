import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LoadingSpinnerComponent } from './loading-spinner.component';

/**
 * Module for the loading-spinner.
 */
@NgModule({
  declarations: [
    LoadingSpinnerComponent,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    LoadingSpinnerComponent,
  ],
})
export class LoadingSpinnerModule { }
