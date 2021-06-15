import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../components/loading-spinner/loading-spinner.module';
import { SortableTableModule } from '../components/sortable-table/sortable-table.module';
import { LogsPageComponent } from './logs-page.component';

const routes: Routes = [
  {
    path: '',
    component: LogsPageComponent,
  },
];

/**
 * Module for feedback session result page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    SortableTableModule,
  ],
  declarations: [
    LogsPageComponent,
  ],
  exports: [
    LogsPageComponent,
  ],
})
export class LogsPageModule { }