import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../components/loading-spinner/loading-spinner.module';
import { LogsTableModule } from '../components/logs-table/logs-table.module';
import { SortableTableModule } from '../components/sortable-table/sortable-table.module';
import { TimepickerModule } from '../components/timepicker/timepicker.module';
import { LogsPageComponent } from './logs-page.component';

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
    TimepickerModule,
  ],
  declarations: [
    LogsPageComponent,
  ],
  exports: [
    LogsPageComponent,
  ],
})
export class LogsPageModule { }
