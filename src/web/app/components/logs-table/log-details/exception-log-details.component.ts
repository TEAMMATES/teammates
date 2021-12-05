import { Component, Input } from '@angular/core';
import { ExceptionLogDetails, GeneralLogEntry, LogEvent } from '../../../../types/api-output';

/**
 * Additional details for exception logs.
 */
@Component({
  selector: 'tm-exception-log-details',
  templateUrl: './exception-log-details.component.html',
  styleUrls: ['./exception-log-details.component.scss', './log-details.component.scss'],
})
export class ExceptionLogDetailsComponent {

  logValue!: GeneralLogEntry;
  details!: Partial<ExceptionLogDetails>;
  exceptionStackTrace!: string;

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
    if (log.details && log.details.event === LogEvent.EXCEPTION_LOG) {
      const details: ExceptionLogDetails = JSON.parse(JSON.stringify(log.details)) as ExceptionLogDetails;
      this.exceptionStackTrace = this.createStackTraceString(details);

      this.details = details;
      if (this.exceptionStackTrace) {
        this.details.exceptionClasses = undefined;
        this.details.exceptionMessages = undefined;
        this.details.exceptionStackTraces = undefined;
      }
    }
  }

  private createStackTraceString(details: ExceptionLogDetails): string {
    if (details.exceptionClasses.length !== details.exceptionStackTraces.length) {
      return '';
    }
    if (details.exceptionMessages && details.exceptionClasses.length !== details.exceptionMessages.length) {
      return '';
    }

    const stackTrace: string[] = [];
    for (let i: number = 0; i < details.exceptionClasses.length; i += 1) {
      let firstLine: string = details.exceptionClasses[i];
      if (details.exceptionMessages && details.exceptionMessages[i]) {
        firstLine += `: ${details.exceptionMessages[i]}`;
      }
      stackTrace.push(firstLine);
      details.exceptionStackTraces[i].forEach((line: string) => stackTrace.push(`        at ${line}`));
    }

    return stackTrace.join('\r\n');
  }

}
