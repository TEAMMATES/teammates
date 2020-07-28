import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LoadingRetryComponent } from './loading-retry.component';

/**
 * Module for the retry button
 */
@NgModule({
  declarations: [LoadingRetryComponent],
  imports: [
    CommonModule,
  ],
  exports: [LoadingRetryComponent],
})
export class LoadingRetryModule { }
