import { Component, EventEmitter, Input, Output } from '@angular/core';
import { GeneralLogEntry, LogEvent, RequestLogDetails } from '../../../../types/api-output';

/**
 * Request log line.
 */
@Component({
  selector: 'tm-request-log-line',
  templateUrl: './request-log-line.component.html',
  styleUrls: ['./request-log-line.component.scss', './log-line.component.scss'],
})
export class RequestLogLineComponent {

  logValue!: GeneralLogEntry;
  responseTime: number = 0;
  httpStatus: number = 0;
  actionClass?: string;
  summary: string = '';

  @Output()
  addActionClassEvent: EventEmitter<string> = new EventEmitter<string>();

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
    if (log.details && log.details.event === LogEvent.REQUEST_LOG) {
      const details: RequestLogDetails = log.details as RequestLogDetails;
      this.httpStatus = details.responseStatus;
      this.responseTime = details.responseTime;
      this.actionClass = details.actionClass;
      this.summary = `${details.requestMethod} ${details.requestUrl}`;
    }
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

  addActionClassToFilter(actionClass: string): void {
    this.addActionClassEvent.emit(actionClass);
  }

}
