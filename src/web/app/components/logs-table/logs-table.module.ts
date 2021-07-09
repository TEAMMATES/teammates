import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { LogsTableComponent } from './logs-table.component';

/**
 * Module for displaying logs in table.
 */
@NgModule({
  declarations: [LogsTableComponent],
  imports: [CommonModule],
  exports: [LogsTableComponent],
})
export class LogsTableModule { }
