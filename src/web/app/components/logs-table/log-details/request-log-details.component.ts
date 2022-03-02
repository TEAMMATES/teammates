import { Component, EventEmitter, Input, Output } from '@angular/core';
import { GeneralLogEntry, LogEvent, RequestLogDetails, RequestLogUser } from '../../../../types/api-output';

/**
 * Additional details for request logs.
 */
@Component({
  selector: 'tm-request-log-details',
  templateUrl: './request-log-details.component.html',
  styleUrls: ['./request-log-details.component.scss', './log-details.component.scss'],
})
export class RequestLogDetailsComponent {

  logValue!: GeneralLogEntry;
  details!: RequestLogDetails;
  userInfo?: RequestLogUser;
  requestBody?: any;

  @Output()
  addUserInfoEvent: EventEmitter<RequestLogUser> = new EventEmitter<RequestLogUser>();

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
    if (log.details && log.details.event === LogEvent.REQUEST_LOG) {
      const details: RequestLogDetails = JSON.parse(JSON.stringify(log.details)) as RequestLogDetails;
      this.userInfo = details.userInfo;
      details.userInfo = undefined;

      if (details.requestBody) {
        try {
          this.requestBody = JSON.parse(details.requestBody);
          details.requestBody = undefined;
        } catch (err) {
          // request body is not JSON; while generally it should not happen, it is not impossible
        }
      }

      this.details = details;
    }
  }

  addUserInfoToFilter(userInfo: RequestLogUser): void {
    this.addUserInfoEvent.emit(userInfo);
  }
}
