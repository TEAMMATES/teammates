import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { EMPTY, forkJoin, Observable } from 'rxjs';
import { concatMap, expand, finalize, map, reduce } from 'rxjs/operators';
import { LogService } from 'src/web/services/log.service';
import { StatusMessageService } from 'src/web/services/status-message.service';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from 'src/web/services/timezone.service';
import { GeneralLogEntry, GeneralLogs } from 'src/web/types/api-output';
import { ApiConst } from '../../../types/api-const';
import { LogsHistogramDataModel } from '../../components/logs-histogram/logs-histogram-model';
import { DateFormat } from '../../components/session-edit-form/session-edit-form-model';
import { TimeFormat } from '../../components/session-edit-form/time-picker/time-picker.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Model for searching of logs
 */
 interface SearchLogsFormModel {
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
  severity: string;
  nextPageToken?: string;
 }

@Component({
  selector: 'tm-logs-histogram-page',
  templateUrl: './logs-histogram-page.component.html',
  styleUrls: ['./logs-histogram-page.component.scss']
})
export class LogsHistogramPageComponent implements OnInit {

  readonly LOGS_RETENTION_PERIOD_IN_DAYS: number = ApiConst.LOGS_RETENTION_PERIOD;
  readonly LOGS_RETENTION_PERIOD_IN_MILLISECONDS: number = this.LOGS_RETENTION_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
  
  formModel: SearchLogsFormModel = {
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
  }
  previousQueryParams: QueryParams = {
    searchFrom: '',
    searchUntil: '',
    severity: '',
  }
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  nextPageToken: string | undefined = '';
  isLoading: boolean = false;
  isSearching: boolean = false;
  searchResult: LogsHistogramDataModel[] = [];

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
    this.isSearching = true;
    this.searchResult = [];
    const localDateTime: Observable<number>[] = [
      this.resolveLocalDateTime(this.formModel.logsDateFrom, this.formModel.logsTimeFrom, 'Search period from'),
      this.resolveLocalDateTime(this.formModel.logsDateTo, this.formModel.logsTimeTo, 'Search period until'),
    ];
    forkJoin(localDateTime)
      .pipe(
        concatMap(([timestampFrom, timestampUntil]: number[]) => {
          this.previousQueryParams = {
            searchFrom: timestampFrom.toString(),
            searchUntil: timestampUntil.toString(),
            severity: 'ERROR', 
          }
          return this.logService.searchLogs(this.previousQueryParams);
        }),
        finalize(() => {
          this.isSearching = false;
          // this.hasResult = true;
        }))
      .pipe(
        expand((logs: GeneralLogs) => {
          if (logs.nextPageToken !== undefined) {
            this.previousQueryParams.nextPageToken = logs.nextPageToken;
            return this.logService.searchLogs(this.previousQueryParams);
          }

          return EMPTY;
        }),
        reduce((acc, res) => acc.concat(res.logEntries), [] as GeneralLogEntry[])
      )
      .subscribe((logResults: GeneralLogEntry[]) => this.processLogs(logResults),
        (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  private processLogs(logs: GeneralLogEntry[]): void {
    const map: Map<string, number> = logs.reduce((acc, log) =>
      acc.set(JSON.stringify(log.sourceLocation), (acc.get(JSON.stringify(log.sourceLocation)) || 0) + 1),
      new Map<string, number>());
    map.forEach((value: number, key: string) => {
      this.searchResult.push({ sourceLocation: JSON.parse(key), numberOfTimes: value });
    });
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
}
