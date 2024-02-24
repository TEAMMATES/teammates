import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { LogsPageComponent } from './logs-page.component';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { LogsHistogramModule } from '../../components/logs-histogram/logs-histogram.module';
import { LogsTableModule } from '../../components/logs-table/logs-table.module';
import { SortableTableModule } from '../../components/sortable-table/sortable-table.module';

const routes: Routes = [
  {
    path: '',
    component: LogsPageComponent,
  },
];

/**
 * Module for log page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    SortableTableModule,
    LogsTableModule,
    NgbDatepickerModule,
    NgbTimepickerModule,
    LogsHistogramModule,
  ],
  declarations: [
    LogsPageComponent,
  ],
  exports: [
    LogsPageComponent,
  ],
})
export class LogsPageModule { }
