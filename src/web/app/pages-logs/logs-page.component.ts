import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { concatMap, finalize, map } from 'rxjs/operators';
import { LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from '../../services/timezone.service';
import { ApiConst } from '../../types/api-const';
import { ActionClasses, GeneralLogEntry, GeneralLogs, SourceLocation } from '../../types/api-output';
import { LogsTableRowModel } from '../components/logs-table/logs-table-model';
import { DateFormat } from '../components/session-edit-form/session-edit-form-model';
import { TimeFormat } from '../components/session-edit-form/time-picker/time-picker.component';
import { collapseAnim } from '../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * Model for searching of logs.
 */
interface SearchLogsFormModel {
  logsSeverity: string;
  logsMinSeverity: string;
  logsEvent: string;
  logsFilter: string;
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
}

/**
 * Query parameters for logs endpoint.
 */
export interface QueryParams {
  searchFrom: string;
  searchUntil: string;
  severity?: string;
  minSeverity?: string;
  logEvent?: string;
  nextPageToken?: string;
  actionClass?: string;
  traceId?: string;
  googleId?: string;
  regkey?: string;
  email?: string;
  sourceLocationFile?: string;
  sourceLocationFunction?: string;
  exceptionClass?: string;
}

/**
 * Admin and maintainer logs page.
 */
@Component({
  selector: 'tm-logs-page',
  templateUrl: './logs-page.component.html',
  styleUrls: ['./logs-page.component.scss'],
  animations: [collapseAnim],
})
export class LogsPageComponent implements OnInit {
  readonly LOGS_RETENTION_PERIOD_IN_DAYS: number = ApiConst.LOGS_RETENTION_PERIOD;
  readonly LOGS_RETENTION_PERIOD_IN_MILLISECONDS: number = this.LOGS_RETENTION_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
  readonly SEVERITIES: string[] = ['INFO', 'WARNING', 'ERROR'];
  readonly EVENTS: string[] = ['REQUEST_RECEIVED', 'RESPONSE_DISPATCHED', 'EMAIL_SENT', 'FEEDBACK_SESSION_AUDIT'];
  readonly SEVERITY: string = 'severity';
  readonly MIN_SEVERITY: string = 'minSeverity';
  readonly EVENT: string = 'event';
  ACTION_CLASSES: string[] = [];

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
  queryParams: QueryParams = { searchFrom: '', searchUntil: '' };
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
    this.isLoading = true;
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

    this.logService.getActionClassList()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe((actionClasses: ActionClasses) => this.ACTION_CLASSES = actionClasses.actionClasses.sort());
  }

  searchForLogs(): void {
    if (!this.isFormValid()) {
      return;
    }

    this.hasResult = false;
    this.isSearching = true;
    this.searchResults = [];
    this.nextPageToken = '';
    this.isFiltersExpanded = false;
    const localDateTime: Observable<number>[] = [
      this.resolveLocalDateTime(this.formModel.logsDateFrom, this.formModel.logsTimeFrom, 'Search period from'),
      this.resolveLocalDateTime(this.formModel.logsDateTo, this.formModel.logsTimeTo, 'Search period until'),
    ];

    forkJoin(localDateTime)
      .pipe(
        concatMap(([timestampFrom, timestampUntil]: number[]) => {
          this.setQueryParams(timestampFrom, timestampUntil);
          return this.logService.searchLogs(this.queryParams);
        }),
        finalize(() => {
          this.isSearching = false;
          this.hasResult = true;
        }))
      .subscribe((generalLogs: GeneralLogs) => this.processLogs(generalLogs),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private isFormValid(): boolean {
    if (this.formModel.logsFilter === '') {
      this.statusMessageService.showErrorToast('Please choose to filter by severity / minimum severity / event');
      return false;
    }
    if (this.formModel.logsFilter === this.SEVERITY && this.formModel.logsSeverity === '') {
      this.statusMessageService.showErrorToast('Please choose a severity level');
      return false;
    }
    if (this.formModel.logsFilter === this.MIN_SEVERITY && this.formModel.logsMinSeverity === '') {
      this.statusMessageService.showErrorToast('Please choose a minimum severity level');
      return false;
    }
    if (this.formModel.logsFilter === this.EVENT && this.formModel.logsEvent === '') {
      this.statusMessageService.showErrorToast('Please choose an event type');
      return false;
    }
    if (!this.queryParams.sourceLocationFile && this.queryParams.sourceLocationFunction) {
      this.isFiltersExpanded = true;
      this.statusMessageService.showErrorToast('Please fill in Source location file or clear Source location function');
      return false;
    }

    return true;
  }

  private setQueryParams(timestampFrom: number, timestampUntil: number): void {
    this.queryParams.searchFrom = timestampFrom.toString();
    this.queryParams.searchUntil = timestampUntil.toString();
    this.queryParams.nextPageToken = undefined;

    if (this.formModel.logsFilter === this.SEVERITY) {
      this.queryParams.severity = this.formModel.logsSeverity;
      this.queryParams.minSeverity = undefined;
      this.queryParams.logEvent = undefined;
    }

    if (this.formModel.logsFilter === this.MIN_SEVERITY) {
      this.queryParams.minSeverity = this.formModel.logsMinSeverity;
      this.queryParams.severity = undefined;
      this.queryParams.logEvent = undefined;
    }

    if (this.formModel.logsFilter === this.EVENT) {
      this.queryParams.logEvent = this.formModel.logsEvent;
      this.queryParams.minSeverity = undefined;
      this.queryParams.severity = undefined;
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
    let userInfo: any;
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
      if (payload.userInfo) {
        userInfo = payload.userInfo;
        payload.userInfo = undefined; // Removed so that userInfo is not displayed twice
      }
    }
    return {
      summary,
      httpStatus,
      responseTime,
      userInfo,
      traceId: log.trace,
      sourceLocation: log.sourceLocation,
      timestamp: this.timezoneService.formatToString(log.timestamp, this.timezoneService.guessTimezone(), 'DD MMM, YYYY hh:mm:ss A'),
      severity: log.severity,
      details: payload,
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
    this.queryParams.nextPageToken = this.nextPageToken;
    this.logService.searchLogs(this.queryParams)
      .pipe(finalize(() => this.isSearching = false))
      .subscribe((generalLogs: GeneralLogs) => this.processLogs(generalLogs),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  addTraceToFilter(trace: string): void {
    this.isFiltersExpanded = true;
    this.queryParams.traceId = trace;
    this.statusMessageService.showSuccessToast('Trace ID added to filters');
  }

  addSourceLocationToFilter(sourceLocation: SourceLocation): void {
    this.isFiltersExpanded = true;
    this.queryParams.sourceLocationFile = sourceLocation.file;
    this.queryParams.sourceLocationFunction = sourceLocation.function;
    this.statusMessageService.showSuccessToast('Source location added to filters');
  }

  addUserInfoToFilter(userInfo: any): void {
    this.isFiltersExpanded = true;
    if (userInfo.googleId) {
      this.queryParams.googleId = userInfo.googleId;
    } else if (userInfo.regkey) {
      this.queryParams.regkey = userInfo.regkey;
    } else if (userInfo.email) {
      this.queryParams.email = userInfo.email;
    }

    this.statusMessageService.showSuccessToast('User info added to filters');
  }

  clearFilters(): void {
    this.queryParams.traceId = '';
    this.queryParams.googleId = '';
    this.queryParams.regkey = '';
    this.queryParams.email = '';
    this.queryParams.actionClass = '';
    this.queryParams.sourceLocationFile = '';
    this.queryParams.sourceLocationFunction = '';
    this.queryParams.exceptionClass = '';
  }
}
