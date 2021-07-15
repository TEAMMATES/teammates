import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LogsHistogramComponent } from './logs-histogram.component';

/**
 * Module for displaying logs in histogram.
 */
@NgModule({
  declarations: [LogsHistogramComponent],
  imports: [CommonModule],
  exports: [LogsHistogramComponent],
})
export class LogsHistogramModule { }
