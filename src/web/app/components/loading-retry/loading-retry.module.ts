import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LoadingRetryComponent } from './loading-retry.component';

/**
 * Module for the retry button
 */
@NgModule({
  imports: [
    CommonModule,
    LoadingRetryComponent,
  ],
  exports: [LoadingRetryComponent],
})
export class LoadingRetryModule { }
