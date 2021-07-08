import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SourceLocation } from 'src/web/types/api-output';
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
  @Output()
  addSourceLocationEvent: EventEmitter<SourceLocation> = new EventEmitter<SourceLocation>();

  constructor() { }

  ngOnInit(): void {
  }

  expandDetails(logsTableRowModel: LogsTableRowModel): void {
    logsTableRowModel.isDetailsExpanded = !logsTableRowModel.isDetailsExpanded;
  }

  getClassForStatus(httpStatus: number): string {
    const num: number = Math.floor(httpStatus / 100);
    switch (num) {
      case 2:
        return 'green-font';
      case 4:
        return 'orange-font';
      case 5:
        return 'red-font';
      default:
        return '';
    }
  }

  getClassForSeverity(severity: string): string {
    switch (severity) {
      case 'INFO':
        return 'info-row';
      case 'WARNING':
        return 'warning-row';
      case 'ERROR':
        return 'error-row';
      default:
        return '';
    }
  }

  addTraceToFilter(trace: string): void {
    this.addTraceEvent.emit(trace);
  }

  addSourceLocationToFilter(sourceLocation: SourceLocation): void {
    this.addSourceLocationEvent.emit(sourceLocation);
  }
}
