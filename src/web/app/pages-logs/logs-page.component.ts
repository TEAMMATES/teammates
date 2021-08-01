import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EMPTY } from 'rxjs';
import { expand, finalize, reduce, tap } from 'rxjs/operators';
import { LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TimezoneService } from '../../services/timezone.service';
import { ApiConst } from '../../types/api-const';
import {
  ActionClasses,
  GeneralLogEntry,
  GeneralLogs,
  LogEvent,
  LogSeverity,
  QueryLogsParams,
  RequestLogUser,
  SourceLocation,
} from '../../types/api-output';
import { DateFormat } from '../components/datepicker/datepicker.component';
import { LogsHistogramDataModel } from '../components/logs-histogram/logs-histogram-model';
import { LogsTableRowModel } from '../components/logs-table/logs-table-model';
import { collapseAnim } from '../components/teammates-common/collapse-anim';
import { TimeFormat } from '../components/timepicker/timepicker.component';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * Model for searching of logs.
 */
interface SearchLogsFormModel {
  logsFilter: string;
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
  filters: Partial<QueryLogsParams>;
}

const MAXIMUM_PAGES_FOR_ERROR_LOGS: number = 20;
const TEN_MINUTES_IN_MILLISECONDS: number = 10 * 60 * 1000;
const ASCENDING_ORDER: string = 'asc';
const DESCENDING_ORDER: string = 'desc';

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
  readonly SEVERITIES: LogSeverity[] = [
    LogSeverity.INFO, LogSeverity.WARNING, LogSeverity.ERROR,
  ];
  readonly EVENTS: LogEvent[] = [
    LogEvent.REQUEST_LOG, LogEvent.EXCEPTION_LOG, LogEvent.EMAIL_SENT, LogEvent.FEEDBACK_SESSION_AUDIT,
  ];
  readonly SEVERITY: string = 'severity';
  readonly MIN_SEVERITY: string = 'minSeverity';
  readonly EVENT: string = 'event';
  ACTION_CLASSES: string[] = [];
  isAdmin: boolean = false;

  formModel: SearchLogsFormModel = {
    logsFilter: this.EVENT,
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
    filters: {
      startTime: 0,
      endTime: 0,
      severity: 'INFO',
      minSeverity: 'INFO',
      logEvent: LogEvent.REQUEST_LOG,
      actionClass: '',
      exceptionClass: '',
      extraFilters: '',
      latency: '',
      order: '',
      pageSize: 50,
      sourceLocation: {
        file: '',
        line: 0,
        function: '',
      },
      status: '',
      traceId: '',
      userInfoParams: {
        googleId: '',
        email: '',
        regkey: '',
      },
    },
  };
  queryParams: Partial<QueryLogsParams> = { startTime: 0, endTime: 0 };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  searchResults: LogsTableRowModel[] = [];
  histogramResult: LogsHistogramDataModel[] = [];
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = false;
  isTableView: boolean = true;
  isFiltersExpanded: boolean = false;
  searchStartTime: number = 0;
  searchEndTime: number = 0;
  earliestLogTimestampRetrieved: number = Number.MAX_SAFE_INTEGER;
  latestLogTimestampRetrieved: number = 0;
  hasPreviousPage: boolean = true;
  hasNextPage: boolean = false;
  logsMap: Map<string, number> = new Map<string, number>();

  constructor(private logService: LogService,
    private timezoneService: TimezoneService,
    private statusMessageService: StatusMessageService,
    private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.isLoading = true;
    const now: Date = new Date();
    this.dateToday.year = now.getFullYear();
    this.dateToday.month = now.getMonth() + 1;
    this.dateToday.day = now.getDate();

    const earliestSearchDate: Date = new Date(now.getTime() - this.LOGS_RETENTION_PERIOD_IN_MILLISECONDS);
    this.earliestSearchDate.year = earliestSearchDate.getFullYear();
    this.earliestSearchDate.month = earliestSearchDate.getMonth() + 1;
    this.earliestSearchDate.day = earliestSearchDate.getDate();

    // Start with logs from the past hour
    const fromDate: Date = new Date(now.getTime() - 60 * 60 * 1000);

    this.formModel.logsDateFrom = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };
    this.formModel.logsDateTo = { ...this.dateToday };
    this.formModel.logsTimeFrom = { hour: fromDate.getHours(), minute: fromDate.getMinutes() };
    this.formModel.logsTimeTo = { hour: now.getHours(), minute: now.getMinutes() };

    this.logService.getActionClassList()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe((actionClasses: ActionClasses) => this.ACTION_CLASSES = actionClasses.actionClasses.sort());

    this.activatedRoute.data.pipe(
        tap((data: any) => {
          this.isAdmin = data.isAdmin;
        }),
    ).subscribe(() => {});
  }

  searchForLogs(): void {
    if (this.isTableView && !this.isFormValid()) {
      return;
    }

    this.hasResult = false;
    this.isSearching = true;
    this.histogramResult = [];
    this.searchResults = [];
    this.logsMap = new Map<string, number>();
    this.earliestLogTimestampRetrieved = Number.MAX_SAFE_INTEGER;
    this.latestLogTimestampRetrieved = 0;
    this.hasPreviousPage = true;
    this.hasNextPage = false;
    const timestampFrom: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateFrom, this.formModel.logsTimeFrom);
    const timestampUntil: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateTo, this.formModel.logsTimeTo);

    if (this.isTableView) {
      this.searchForLogsTableView(timestampFrom, timestampUntil);
    } else {
      this.searchForLogsHistogramView(timestampFrom, timestampUntil);
    }
  }

  private searchForLogsTableView(timestampFrom: number, timestampUntil: number): void {
    this.searchStartTime = timestampFrom;
    this.searchEndTime = timestampUntil;
    this.setQueryParams(timestampFrom, timestampUntil);
    this.logService.searchLogs(this.queryParams)
      .pipe(
        finalize(() => {
          this.isSearching = false;
          this.hasResult = true;
        }))
      .subscribe((generalLogs: GeneralLogs) => {
        this.hasPreviousPage = generalLogs.hasNextPage;
        this.processLogsForTableView(generalLogs, true);
      }, (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private isFormValid(): boolean {
    if (this.formModel.logsFilter === '') {
      this.statusMessageService.showErrorToast('Please choose to filter by severity / minimum severity / event');
      return false;
    }
    if (this.formModel.logsFilter === this.SEVERITY && this.formModel.filters.severity === '') {
      this.statusMessageService.showErrorToast('Please choose a severity level');
      return false;
    }
    if (this.formModel.logsFilter === this.MIN_SEVERITY && this.formModel.filters.minSeverity === '') {
      this.statusMessageService.showErrorToast('Please choose a minimum severity level');
      return false;
    }
    if (this.formModel.filters.sourceLocation && !this.formModel.filters.sourceLocation.file
        && this.formModel.filters.sourceLocation.function) {
      this.isFiltersExpanded = true;
      this.statusMessageService.showErrorToast('Please fill in Source location file or clear Source location function');
      return false;
    }

    return true;
  }

  /**
   * Sets the query parameters with the given timestamps and filters in form model.
   */
  private setQueryParams(timestampFrom: number, timestampUntil: number): void {
    this.queryParams = JSON.parse(JSON.stringify(this.formModel.filters));
    this.queryParams.startTime = timestampFrom;
    this.queryParams.endTime = timestampUntil;
    this.queryParams.order = DESCENDING_ORDER;

    if (this.formModel.logsFilter === this.SEVERITY) {
      this.queryParams.severity = this.formModel.filters.severity;
    }

    if (this.formModel.logsFilter === this.MIN_SEVERITY) {
      this.queryParams.minSeverity = this.formModel.filters.minSeverity;
    }

    if (this.formModel.logsFilter === this.EVENT) {
      this.queryParams.logEvent = this.formModel.filters.logEvent;
    }
  }

  private searchForLogsHistogramView(timestampFrom: number, timestampUntil: number): void {
    let numberOfPagesRetrieved: number = 0;
    this.queryParams = {
      startTime: timestampFrom,
      endTime: timestampUntil,
      order: DESCENDING_ORDER,
      severity: 'ERROR',
    };
    this.logService.searchLogs(this.queryParams)
      .pipe(
        expand((logs: GeneralLogs) => {
          if (logs.hasNextPage && numberOfPagesRetrieved < MAXIMUM_PAGES_FOR_ERROR_LOGS) {
            numberOfPagesRetrieved += 1;
            this.queryParams.endTime = logs.logEntries[logs.logEntries.length - 1].timestamp;
            return this.logService.searchLogs(this.queryParams);
          }

          return EMPTY;
        }),
        reduce((acc: GeneralLogEntry[], res: GeneralLogs) => acc.concat(res.logEntries), [] as GeneralLogEntry[]),
        finalize(() => {
          this.isSearching = false;
          this.hasResult = true;
        }),
      )
      .subscribe((logResults: GeneralLogEntry[]) => this.processLogsForHistogram(logResults),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private processLogsForTableView(generalLogs: GeneralLogs, isDescendingOrder: boolean): void {
    if (isDescendingOrder) {
      generalLogs.logEntries.forEach((log: GeneralLogEntry) => {
        if (this.logsMap.get(log.insertId) === log.timestamp) {
          return;
        }
        this.logsMap.set(log.insertId, log.timestamp);
        this.searchResults.unshift(this.toLogModel(log));
      });
    } else {
      generalLogs.logEntries
        .sort((a: GeneralLogEntry, b: GeneralLogEntry) => a.timestamp - b.timestamp)
        .forEach((log: GeneralLogEntry) => {
          if (this.logsMap.get(log.insertId) === log.timestamp) {
            return;
          }
          this.logsMap.set(log.insertId, log.timestamp);
          this.searchResults.push(this.toLogModel(log));
        });
    }
  }

  private processLogsForHistogram(logs: GeneralLogEntry[]): void {
    const sourceToFrequencyMap: Map<string, number> = logs
      .filter((log: GeneralLogEntry) => {
        if (this.logsMap.get(log.insertId) === log.timestamp) {
          return false;
        }
        this.logsMap.set(log.insertId, log.timestamp);
        return true;
      })
      .reduce((acc: Map<string, number>, log: GeneralLogEntry) =>
        acc.set(JSON.stringify(log.sourceLocation), (acc.get(JSON.stringify(log.sourceLocation)) || 0) + 1),
        new Map<string, number>());
    sourceToFrequencyMap.forEach((value: number, key: string) => {
      this.histogramResult.push({ sourceLocation: JSON.parse(key), numberOfTimes: value });
    });
  }

  private toLogModel(log: GeneralLogEntry): LogsTableRowModel {
    if (log.timestamp < this.earliestLogTimestampRetrieved) {
      this.earliestLogTimestampRetrieved = log.timestamp;
    }
    if (log.timestamp > this.latestLogTimestampRetrieved) {
      this.latestLogTimestampRetrieved = log.timestamp;
    }

    let summary: string = '';
    let actionClass: string = '';
    let exceptionClass: string = '';
    let payload: any = '';
    let httpStatus: number | undefined;
    let responseTime: number | undefined;
    let traceIdForSummary: string | undefined;
    let userInfo: RequestLogUser | undefined;

    if (log.trace) {
      traceIdForSummary = this.formatTraceForSummary(log.trace);
    }

    if (log.message) {
      summary = log.message;
      payload = this.formatTextPayloadForDisplay(log.message);
    } else if (log.details) {
      payload = log.details;
      if (payload.requestMethod) {
        summary += `${payload.requestMethod} `;
      }
      if (payload.requestUrl) {
        summary += `${payload.requestUrl} `;
      }
      if (!summary && payload.message) {
        summary = payload.message;
      }
      if (payload.responseStatus) {
        httpStatus = payload.responseStatus;
      }
      if (payload.responseTime) {
        responseTime = payload.responseTime;
      }
      if (payload.actionClass) {
        actionClass = payload.actionClass;
      }
      if (payload.exceptionClass) {
        exceptionClass = payload.exceptionClass;
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
      actionClass,
      exceptionClass,
      traceIdForSummary,
      timestamp: log.timestamp,
      logName: log.logName,
      insertId: log.insertId,
      trace: log.trace,
      sourceLocation: log.sourceLocation,
      resourceIdentifier: log.resourceIdentifier,
      timestampForDisplay: this.timezoneService.formatToString(
          log.timestamp, this.timezoneService.guessTimezone(), 'DD MMM, YYYY hh:mm:ss A'),
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

  /**
   * Display the first 9 digits of the trace.
   */
  private formatTraceForSummary(trace: string): string | undefined {
    return trace.slice(0, 9);
  }

  addTraceToFilter(trace: string): void {
    this.isFiltersExpanded = true;
    this.formModel.filters.traceId = trace;
    this.statusMessageService.showSuccessToast('Trace ID added to filters');
  }

  addActionClassToFilter(actionClass: string): void {
    this.isFiltersExpanded = true;
    this.formModel.filters.actionClass = actionClass;
    this.statusMessageService.showSuccessToast('Action class added to filters');
  }

  addExceptionClassToFilter(exceptionClass: string): void {
    this.isFiltersExpanded = true;
    this.formModel.filters.exceptionClass = exceptionClass;
    this.statusMessageService.showSuccessToast('Exception class added to filters');
  }

  addSourceLocationToFilter(sourceLocation: SourceLocation): void {
    this.isFiltersExpanded = true;
    this.formModel.filters.sourceLocation = {
      file: sourceLocation.file,
      line: 0,
      function: sourceLocation.function,
    };
    this.statusMessageService.showSuccessToast('Source location added to filters');
  }

  addUserInfoToFilter(userInfo: RequestLogUser): void {
    this.isFiltersExpanded = true;
    this.formModel.filters.userInfoParams = {
      googleId: userInfo.googleId,
      regkey: userInfo.regkey,
      email: userInfo.email,
    };

    this.statusMessageService.showSuccessToast('User info added to filters');
  }

  clearFilters(): void {
    this.formModel.filters.traceId = '';
    this.formModel.filters.userInfoParams = {
      googleId: '',
      regkey: '',
      email: '',
    };
    this.formModel.filters.actionClass = '';
    this.formModel.filters.sourceLocation = {
      file: '',
      line: 0,
      function: '',
    };
    this.formModel.filters.latency = '';
    this.formModel.filters.status = '';
    this.formModel.filters.extraFilters = '';
    this.formModel.filters.exceptionClass = '';
  }

  switchView(): void {
    this.isTableView = !this.isTableView;
    this.hasResult = false;
    this.searchResults = [];
    this.histogramResult = [];
  }

  loadPreviousLogs(): void {
    this.isSearching = true;
    this.queryParams.order = DESCENDING_ORDER;
    this.queryParams.startTime = this.searchStartTime;
    this.queryParams.endTime = this.earliestLogTimestampRetrieved;
    this.searchPreviousLogs();
  }

  loadLaterLogs(): void {
    this.isSearching = true;
    this.queryParams.order = ASCENDING_ORDER;
    this.queryParams.startTime = this.latestLogTimestampRetrieved;
    this.queryParams.endTime = this.searchEndTime;
    this.searchLaterLogs();
  }

  extendStartTime(): void {
    this.isSearching = true;
    this.queryParams.order = DESCENDING_ORDER;
    this.queryParams.startTime = this.searchStartTime;
    this.searchStartTime = this.searchStartTime - TEN_MINUTES_IN_MILLISECONDS;
    this.queryParams.endTime = this.searchStartTime;
    this.searchPreviousLogs();
  }

  extendEndTime(): void {
    this.isSearching = true;
    this.queryParams.order = ASCENDING_ORDER;
    this.queryParams.startTime = this.searchEndTime;
    this.searchEndTime = this.searchEndTime + TEN_MINUTES_IN_MILLISECONDS;
    this.queryParams.endTime = this.searchEndTime;
    this.searchLaterLogs();
  }

  private searchPreviousLogs(): void {
    this.logService.searchLogs(this.queryParams)
      .pipe(finalize(() => this.isSearching = false))
      .subscribe((generalLogs: GeneralLogs) => {
        this.hasPreviousPage = generalLogs.hasNextPage;
        this.processLogsForTableView(generalLogs, true);
      }, (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private searchLaterLogs(): void {
    this.logService.searchLogs(this.queryParams)
      .pipe(finalize(() => this.isSearching = false))
      .subscribe((generalLogs: GeneralLogs) => {
        this.hasNextPage = generalLogs.hasNextPage;
        this.processLogsForTableView(generalLogs, false);
      }, (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }
}
