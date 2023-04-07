import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LoadingSpinnerComponent } from './loading-spinner.component';
import { LoadingSpinnerDirective } from './loading-spinner.directive';

/**
 * Module for the loading spinner
 */
@NgModule({
  declarations: [LoadingSpinnerDirective, LoadingSpinnerComponent],
  imports: [
    CommonModule,
  ],
  exports: [LoadingSpinnerDirective],
})
export class LoadingSpinnerModule { }
