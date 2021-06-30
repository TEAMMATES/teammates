import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { concatMap, finalize, map } from 'rxjs/operators';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from '../../services/timezone.service';
import { ApiConst } from '../../types/api-const';
import { LogService } from '../../services/log.service';
import { DateFormat } from '../components/session-edit-form/session-edit-form-model';
import { TimeFormat } from '../components/session-edit-form/time-picker/time-picker.component';
import { ColumnData, SortableTableCellData } from '../components/sortable-table/sortable-table.component';
// import { StatusMessageService } from 'src/web/services/status-message.service';

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
  searchFrom: string,
  searchUntil: string,
  severities: string,
}

/**
 * Model for displaying of search result.
 */
interface LogResultModel {
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
}

/**
 * Model for storing logs in pages.
 */
interface LogPages {
  logResult: LogResultModel[];
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
  LOGS_RETENTION_PERIOD_IN_DAYS: number = ApiConst.LOGS_RETENTION_PERIOD;
  LOGS_RETENTION_PERIOD_IN_MILLISECONDS: number = this.LOGS_RETENTION_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
  SEVERITIES: string[] = ['INFO', 'WARN', 'ERROR'];

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
  searchResults: LogResultModel[] = [];
  pageResults: LogPages[] = [];
  currentPageNumber: number = 0;
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = false;

  constructor(private logService: LogService,
    private timezoneService: TimezoneService,
    // private statusMessageService: StatusMessageService
    ) { }

  ngOnInit(): void {
    const today: Date = new Date();
    this.dateToday.year = today.getFullYear();
    this.dateToday.month = today.getMonth() + 1;
    this.dateToday.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now() - this.LOGS_RETENTION_PERIOD_IN_MILLISECONDS);
    this.earliestSearchDate.year = earliestSearchDate.getFullYear();
    this.earliestSearchDate.month = earliestSearchDate.getMonth() + 1;
    this.earliestSearchDate.day = earliestSearchDate.getDate();

    this.formModel.logsDateFrom = { ...this.dateToday, day: today.getDate() - 1 };
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
    this.isSearching = true;
    this.searchResults = [];
    this.pageResults = [];
    this.currentPageNumber = 0;
    const localDateTime: Observable<number>[] = [
      this.resolveLocalDateTime(this.formModel.logsDateFrom, this.formModel.logsTimeFrom, 'Search period from'),
      this.resolveLocalDateTime(this.formModel.logsDateTo, this.formModel.logsTimeTo, 'Search period until'),
    ];

    forkJoin(localDateTime)
        .pipe(
            concatMap(([timestampFrom, timestampUntil]: number[]) => {
              return this.logService.searchLogs({
                searchFrom: timestampFrom.toString(),
                searchUntil: timestampUntil.toString(),
                severities: Array.from(this.formModel.logsSeverity).join(','),
              });
            }),
            finalize(() => {
              this.isSearching = false;
              this.hasResult = true;
            }))
            .subscribe();
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

  // toLogModel(log: Log): void {

  // }

  getPreviousPageLogs(): void {
    // TODO
    if (this.currentPageNumber > 0) {
      this.currentPageNumber = this.currentPageNumber - 1;
      this.searchResults = this.pageResults[this.currentPageNumber].logResult;
    }
  }

  getNextPageLogs(): void {
    // TODO
    if (this.pageResults.length > this.currentPageNumber) {
      this.currentPageNumber = this.currentPageNumber + 1;
      this.searchResults = this.pageResults[this.currentPageNumber].logResult;
      return;
    }
    this.logService.searchLogs(this.previousQueryParams)
      .subscribe();
  }
}
