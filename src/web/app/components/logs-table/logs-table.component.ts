import { NgClass, JsonPipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { EmailLogDetailsComponent } from './log-details/email-log-details.component';
import { ExceptionLogDetailsComponent } from './log-details/exception-log-details.component';
import { GenericLogDetailsComponent } from './log-details/generic-log-details.component';
import { RequestLogDetailsComponent } from './log-details/request-log-details.component';
import { ExceptionLogLineComponent } from './log-line/exception-log-line.component';
import { GenericLogLineComponent } from './log-line/generic-log-line.component';
import { RequestLogLineComponent } from './log-line/request-log-line.component';
import { LogsTableRowModel } from './logs-table-model';
import { LogEvent, RequestLogUser, SourceLocation } from '../../../types/api-output';

/**
 * A table to display logs.
 */
@Component({
  selector: 'tm-logs-table',
  templateUrl: './logs-table.component.html',
  styleUrls: ['./logs-table.component.scss'],
  imports: [
    NgClass,
    NgbTooltip,
    RequestLogLineComponent,
    ExceptionLogLineComponent,
    GenericLogLineComponent,
    RequestLogDetailsComponent,
    EmailLogDetailsComponent,
    ExceptionLogDetailsComponent,
    GenericLogDetailsComponent,
    JsonPipe
],
})
export class LogsTableComponent {

  LogEvent: typeof LogEvent = LogEvent;

  @Input()
  logs: LogsTableRowModel[] = [];
  @Input()
  isAdmin: boolean = false;

  @Output()
  addTraceEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output()
  addActionClassEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output()
  addExceptionClassEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output()
  addSourceLocationEvent: EventEmitter<SourceLocation> = new EventEmitter<SourceLocation>();
  @Output()
  addUserInfoEvent: EventEmitter<RequestLogUser> = new EventEmitter<RequestLogUser>();

  expandDetails(logsTableRowModel: LogsTableRowModel): void {
    logsTableRowModel.isDetailsExpanded = !logsTableRowModel.isDetailsExpanded;
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

  addActionClassToFilter(actionClass: string): void {
    this.addActionClassEvent.emit(actionClass);
  }

  addExceptionClassToFilter(exceptionClass: string): void {
    this.addExceptionClassEvent.emit(exceptionClass);
  }

  addSourceLocationToFilter(sourceLocation: SourceLocation): void {
    this.addSourceLocationEvent.emit(sourceLocation);
  }

  addUserInfoToFilter(userInfo: RequestLogUser): void {
    this.addUserInfoEvent.emit(userInfo);
  }
}
