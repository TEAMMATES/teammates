import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { LogDetailsModule } from './log-details/log-details.module';
import { LogLineModule } from './log-line/log-line.module';
import { LogsTableComponent } from './logs-table.component';

/**
 * Module for displaying logs in table.
 */
@NgModule({
  declarations: [LogsTableComponent],
  imports: [CommonModule, NgbTooltipModule, LogLineModule, LogDetailsModule],
  exports: [LogsTableComponent],
})
export class LogsTableModule { }
