import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { concatMap, finalize, map } from 'rxjs/operators';
import { LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from '../../services/timezone.service';
import { ApiConst, ResourceEndpoints } from '../../types/api-const';
import { GeneralLogEntry, GeneralLogs } from '../../types/api-output';
import { LogsTableRowModel } from '../components/logs-table/logs-table-model';
import { DateFormat } from '../components/session-edit-form/session-edit-form-model';
import { TimeFormat } from '../components/session-edit-form/time-picker/time-picker.component';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * Model for searching of logs.
 */
interface SearchLogsFormModel {
  logsSeverity: string;
  logsMinSeverity: string;
  logsEvent: string,
  logsFilter: string;
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
  traceId ?: string;
  userId ?: string;
  apiEndpoint ?: string;
  errorType ?: string;
}

/**
 * Query parameters for HTTP request
 */
interface QueryParams {
  searchFrom: string;
  searchUntil: string;
  severity?: string;
  minSeverity?: string;
  logEvent?: string;
  nextPageToken?: string;
  apiEndpoint?: string,
  traceId?: string,
}

/**
 * Admin and maintainer logs page.
 */
@Component({
  selector: 'tm-logs-page',
  templateUrl: './logs-page.component.html',
  styleUrls: ['./logs-page.component.scss'],
})
export class LogsPageComponent implements OnInit {
  readonly LOGS_RETENTION_PERIOD_IN_DAYS: number = ApiConst.LOGS_RETENTION_PERIOD;
  readonly LOGS_RETENTION_PERIOD_IN_MILLISECONDS: number = this.LOGS_RETENTION_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
  readonly SEVERITIES: string[] = ['INFO', 'WARNING', 'ERROR'];
  readonly EVENTS: string[] = ['REQUEST_RECEIVED', 'RESPONSE_DISPATCHED', 'EMAIL_SENT', 'FEEDBACK_SESSION_AUDIT'];
  readonly SEVERITY: string = 'severity';
  readonly MIN_SEVERITY: string = 'minSeverity';
  readonly EVENT: string = 'event';
  readonly API_ENDPOINTS: string[] = Object.values(ResourceEndpoints);

  formModel: SearchLogsFormModel = {
    logsSeverity: '',
    logsMinSeverity: '',
    logsEvent: '',
    logsFilter: '',
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
  };
  previousQueryParams: QueryParams = { searchFrom: '', searchUntil: '', severity: '' };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  searchResults: LogsTableRowModel[] = [];
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = false;
  nextPageToken: string = '';
  isFiltersExpanded: boolean = false;

  constructor(private logService: LogService,
    private timezoneService: TimezoneService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    const today: Date = new Date();
    this.dateToday.year = today.getFullYear();
    this.dateToday.month = today.getMonth() + 1;
    this.dateToday.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now() - this.LOGS_RETENTION_PERIOD_IN_MILLISECONDS);
    this.earliestSearchDate.year = earliestSearchDate.getFullYear();
    this.earliestSearchDate.month = earliestSearchDate.getMonth() + 1;
    this.earliestSearchDate.day = earliestSearchDate.getDate();

    const fromDate: Date = new Date();
    fromDate.setDate(today.getDate() - 1);

    this.formModel.logsDateFrom = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };
    this.formModel.logsDateTo = { ...this.dateToday };
    this.formModel.logsTimeFrom = { hour: 23, minute: 59 };
    this.formModel.logsTimeTo = { hour: 23, minute: 59 };
  }

  searchForLogs(): void {
    if (this.formModel.logsFilter === ''
      || (this.formModel.logsFilter === this.SEVERITY && this.formModel.logsSeverity === '')
      || (this.formModel.logsFilter === this.MIN_SEVERITY && this.formModel.logsMinSeverity === '')
      || (this.formModel.logsFilter === this.EVENT && this.formModel.logsEvent === '')) {
      this.statusMessageService.showErrorToast('Please select severity level / event');
      return;
    }

    this.hasResult = false;
    this.isSearching = true;
    this.searchResults = [];
    this.nextPageToken = '';
    const localDateTime: Observable<number>[] = [
      this.resolveLocalDateTime(this.formModel.logsDateFrom, this.formModel.logsTimeFrom, 'Search period from'),
      this.resolveLocalDateTime(this.formModel.logsDateTo, this.formModel.logsTimeTo, 'Search period until'),
    ];

    forkJoin(localDateTime)
        .pipe(
            concatMap(([timestampFrom, timestampUntil]: number[]) => {
              this.setQueryParams(timestampFrom, timestampUntil);
              return this.logService.searchLogs(this.previousQueryParams);
            }),
            finalize(() => {
              this.isSearching = false;
              this.hasResult = true;
            }))
            .subscribe((generalLogs: GeneralLogs) => this.processLogs(generalLogs),
              (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private setQueryParams(timestampFrom: number, timestampUntil: number): void {
    this.previousQueryParams = {
      searchFrom: timestampFrom.toString(),
      searchUntil: timestampUntil.toString(),
    }

    if (this.formModel.logsFilter === this.SEVERITY) {
      this.previousQueryParams.severity = this.formModel.logsSeverity;
    }
    
    if (this.formModel.logsFilter === this.MIN_SEVERITY) {
      this.previousQueryParams.minSeverity = this.formModel.logsMinSeverity;
    }

    if (this.formModel.logsFilter === this.EVENT) {
      this.previousQueryParams.logEvent = this.formModel.logsEvent;
    }

    if (this.formModel.apiEndpoint) {
      this.previousQueryParams.apiEndpoint = this.formModel.apiEndpoint;
    }

    if (this.formModel.traceId) {
      this.previousQueryParams.traceId = this.formModel.traceId;
    }
  }

  private processLogs(generalLogs: GeneralLogs): void {
    this.nextPageToken = generalLogs.nextPageToken || '';
    generalLogs.logEntries.forEach((log: GeneralLogEntry) => this.searchResults.push(this.toLogModel(log)));
  }

  private resolveLocalDateTime(date: DateFormat, time: TimeFormat, fieldName: string): Observable<number> {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1);
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);
    const localDateTime: string = inst.format(LOCAL_DATE_TIME_FORMAT);

    return this.timezoneService.getResolvedTimestamp(localDateTime, this.timezoneService.guessTimezone(), fieldName)
        .pipe(map((result: TimeResolvingResult) => result.timestamp));
  }

  toLogModel(log: GeneralLogEntry): LogsTableRowModel {
    let summary: string = '';
    let payload: any = '';
    let httpStatus: number | undefined;
    let responseTime: number | undefined;
    if (log.message) {
      summary = `Source: ${log.sourceLocation.file}`;
      payload = this.formatTextPayloadForDisplay(log.message);
    } else if (log.details) {
      payload = log.details;
      if (payload.requestMethod) {
        summary += `${payload.requestMethod} `;
      }
      if (payload.requestUrl) {
        summary += `${payload.requestUrl} `;
      }
      if (payload.responseStatus) {
        httpStatus = payload.responseStatus;
      }
      if (payload.responseTime) {
        responseTime = payload.responseTime;
      }
      if (payload.actionClass) {
        summary += `${payload.actionClass}`;
      }
    }
    return {
      summary,
      httpStatus,
      responseTime,
      traceId: log.trace,
      timestamp: this.timezoneService.formatToString(log.timestamp, this.timezoneService.guessTimezone(), 'DD MMM, YYYY hh:mm:ss A'),
      severity: log.severity,
      details: JSON.parse(JSON.stringify({
        payload,
        sourceLocation: log.sourceLocation,
      })),
      isDetailsExpanded: false,
    };
  }

  private formatTextPayloadForDisplay(textPayload: String): String {
    return textPayload
      .replace(/\n/g, '<br/>')
      .replace(/\t/g, '&#9;');
  }

  getNextPageLogs(): void {
    this.isSearching = true;
    this.previousQueryParams.nextPageToken = this.nextPageToken;
    this.logService.searchLogs(this.previousQueryParams)
      .pipe(finalize(() => this.isSearching = false))
      .subscribe((generalLogs: GeneralLogs) => this.processLogs(generalLogs),
      (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  addTraceToFilter(trace: string): void {
    this.isFiltersExpanded = true;
    this.formModel.traceId = trace;
    this.statusMessageService.showSuccessToast('Trace ID added to filters');
  }
}
