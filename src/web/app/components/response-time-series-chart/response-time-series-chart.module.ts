import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ResponseTimeSeriesChartComponent } from './response-time-series-chart.component';

/**
 * Module for response time series chart.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
  ],
  declarations: [
    ResponseTimeSeriesChartComponent,
  ],
  exports: [
    ResponseTimeSeriesChartComponent,
  ],
})
export class ResponseTimeSeriesChartModule { }
