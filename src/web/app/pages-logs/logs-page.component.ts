import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { LogService } from '../../services/log.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TimezoneService } from '../../services/timezone.service';
import { ApiConst } from '../../types/api-const';
import { GeneralLogEntry, GeneralLogs } from '../../types/api-output';
import { DateFormat } from '../components/datepicker/datepicker.component';
import { LogsTableRowModel } from '../components/logs-table/logs-table-model';
import { TimeFormat } from '../components/timepicker/timepicker.component';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * Model for searching of logs.
 */
interface SearchLogsFormModel {
  logsSeverity: Set<string>;
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
}

/**
 * Query parameters for HTTP request
 */
interface QueryParams {
  searchFrom: string;
  searchUntil: string;
  severities: string;
  nextPageToken?: string;
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

  formModel: SearchLogsFormModel = {
    logsSeverity: new Set(),
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
  };
  previousQueryParams: QueryParams = { searchFrom: '', searchUntil: '', severities: '' };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  searchResults: LogsTableRowModel[] = [];
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = false;
  nextPageToken: string = '';

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

  toggleSelection(severity: string): void {
    this.formModel.logsSeverity.has(severity)
      ? this.formModel.logsSeverity.delete(severity)
      : this.formModel.logsSeverity.add(severity);
  }

  searchForLogs(): void {
    if (this.formModel.logsSeverity.size === 0) {
      this.statusMessageService.showErrorToast('Please select at least one severity level');
      return;
    }

    this.hasResult = false;
    this.isSearching = true;
    this.searchResults = [];
    this.nextPageToken = '';
    const searchFrom: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateFrom, this.formModel.logsTimeFrom);
    const searchUntil: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateTo, this.formModel.logsTimeTo);

    this.previousQueryParams = {
      searchFrom: searchFrom.toString(),
      searchUntil: searchUntil.toString(),
      severities: Array.from(this.formModel.logsSeverity).join(','),
    };
    this.logService.searchLogs(this.previousQueryParams)
        .pipe(
            finalize(() => {
              this.isSearching = false;
              this.hasResult = true;
            }),
        ).subscribe((generalLogs: GeneralLogs) => {
          this.processLogs(generalLogs);
        }, (e: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(e.error.message);
        });
  }

  private processLogs(generalLogs: GeneralLogs): void {
    this.nextPageToken = generalLogs.nextPageToken || '';
    generalLogs.logEntries.forEach((log: GeneralLogEntry) => this.searchResults.push(this.toLogModel(log)));
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
        summary += `${payload.requestUrl}`;
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
      timestamp: this.timezoneService.formatToString(log.timestamp, this.timezoneService.guessTimezone(), 'DD MMM, YYYY hh:mm:ss A'),
      severity: log.severity,
      details: JSON.parse(JSON.stringify({
        payload,
        sourceLocation: log.sourceLocation,
        trace: log.trace,
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
}
