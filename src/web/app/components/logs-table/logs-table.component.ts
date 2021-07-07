import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
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

  @Output()
  addTraceEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
  }

  expandDetails(logsTableRowModel: LogsTableRowModel): void {
    logsTableRowModel.isDetailsExpanded = !logsTableRowModel.isDetailsExpanded;
  }

  getClassForStatus(httpStatus: number): string {
    const num: number = Math.floor(httpStatus / 100);
    if (num === 2) {
      return 'green-font';
    }
    if (num === 4) {
      return 'orange-font';
    }
    if (num === 5) {
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

  addTraceToFilter(trace: string): void {
    this.addTraceEvent.emit(trace);
  }
}
