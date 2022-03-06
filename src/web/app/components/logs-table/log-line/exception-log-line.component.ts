import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ExceptionLogDetails, GeneralLogEntry, LogEvent } from '../../../../types/api-output';

/**
 * Exception log line.
 */
@Component({
  selector: 'tm-exception-log-line',
  templateUrl: './exception-log-line.component.html',
  styleUrls: ['./exception-log-line.component.scss', './log-line.component.scss'],
})
export class ExceptionLogLineComponent {

  logValue!: GeneralLogEntry;
  exceptionClass: string = '';
  summary: string = '';

  @Output()
  addExceptionClassEvent: EventEmitter<string> = new EventEmitter<string>();

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
    if (log.details && log.details.event === LogEvent.EXCEPTION_LOG) {
      const details: ExceptionLogDetails = log.details as ExceptionLogDetails;
      this.exceptionClass = details.exceptionClass;
      this.summary = details.message || '';
    }
  }

  addExceptionClassToFilter(exceptionClass: string): void {
    this.addExceptionClassEvent.emit(exceptionClass);
  }

}
