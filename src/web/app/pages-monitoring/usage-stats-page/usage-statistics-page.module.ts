import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { StatsLineChartComponent } from './stats-line-chart/stats-line-chart.component';
import { UsageStatisticsPageComponent } from './usage-statistics-page.component';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

const routes: Routes = [
  {
    path: '',
    component: UsageStatisticsPageComponent,
  },
];

/**
 * Module for usage statistics page.
 */
@NgModule({
  declarations: [
    UsageStatisticsPageComponent,
    StatsLineChartComponent,
  ],
  exports: [
    UsageStatisticsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgbDatepickerModule,
    NgbTimepickerModule,
    FormsModule,
    LoadingSpinnerModule,
  ],
})
export class UsageStatisticsPageModule { }
