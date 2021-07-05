import { Component, Input, OnInit } from '@angular/core';
import { LogsTableRowModel } from './logs-table-model';

/**
 * A table to display logs.
 */
@Component({
  selector: 'tm-logs-table',
  templateUrl: './logs-table.component.html',
  styleUrls: ['./logs-table.component.scss'],
})
export class LogsTableComponent implements OnInit {

  @Input()
  logs: LogsTableRowModel[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  expandDetails(logsTableRowModel: LogsTableRowModel): void {
    logsTableRowModel.isDetailsExpanded = !logsTableRowModel.isDetailsExpanded;
  }

  getClassForStatus(httpStatus: number): string {
    if (Math.floor(httpStatus / 100) === 2) {
      return 'green-font';
    }
    if (Math.floor(httpStatus / 100) === 4) {
      return 'orange-font';
    }
    if (Math.floor(httpStatus / 100) === 5) {
      return 'red-font';
    }
    return '';
  }

  getClassForSeverity(severity: string): string {
    if (severity === 'INFO') {
      return 'info-row';
    }
    if (severity === 'WARNING') {
      return 'warning-row';
    }
    if (severity === 'ERROR') {
      return 'error-row';
    }
    return '';
  }
}
