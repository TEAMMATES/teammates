import { Component, OnInit } from '@angular/core';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { UsageStatisticsService } from '../../../services/usage-statistics.service';
import { UsageStatistics, UsageStatisticsRange } from '../../../types/api-output';
import {
  getDefaultDateFormat,
  DateFormat,
  TimeFormat,
  getDefaultTimeFormat,
  Milliseconds,
} from '../../../types/datetime-const';
import { ErrorMessageOutput } from '../../error-message-output';

export enum StatisticsType {
  NUM_RESPONSES,
  NUM_COURSES,
  NUM_STUDENTS,
  NUM_INSTRUCTORS,
  NUM_ACCOUNT_REQUESTS,
  NUM_EMAILS,
  NUM_SUBMISSIONS,
}

export enum AggregationType {
  HOURLY,
  DAILY,
}

interface FormQueryModel {
  fromDate: DateFormat;
  fromTime: TimeFormat;
  toDate: DateFormat;
  toTime: TimeFormat;
  dataType: StatisticsType;
  aggregationType: AggregationType;
}

export interface DataPoint {
  value: number;
  date: string;
}

/**
 * Usage statistics page.
 */
@Component({
  selector: 'tm-usage-statistics-page',
  templateUrl: './usage-statistics-page.component.html',
  styleUrls: ['./usage-statistics-page.component.scss'],
})
export class UsageStatisticsPageComponent implements OnInit {

  StatisticsType = StatisticsType;
  AggregationType = AggregationType;

  itemName = 'responses';

  formModel: FormQueryModel = {
    fromDate: getDefaultDateFormat(),
    fromTime: getDefaultTimeFormat(),
    toDate: getDefaultDateFormat(),
    toTime: getDefaultTimeFormat(),
    dataType: StatisticsType.NUM_RESPONSES,
    aggregationType: AggregationType.HOURLY,
  };
  dateToday: DateFormat = getDefaultDateFormat();
  earliestSearchDate: DateFormat = { year: 2016, month: 1, day: 1 };
  timeRange: { startTime: number, endTime: number } = { startTime: 0, endTime: 0 };
  hasQueried = false;
  isLoading = false;
  fetchedData: UsageStatistics[] = [];
  dataToDraw: DataPoint[] = [];
  timezone = 'UTC';

  constructor(
    private usageStatisticsService: UsageStatisticsService,
    private timezoneService: TimezoneService,
    private statusMessageService: StatusMessageService,
  ) {}

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone();

    const now = new Date();
    this.dateToday.year = now.getFullYear();
    this.dateToday.month = now.getMonth() + 1;
    this.dateToday.day = now.getDate();

    // Start with statistics from the past week
    const fromDate = new Date(now.getTime() - Milliseconds.IN_ONE_WEEK);

    this.formModel.fromDate = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };
    this.formModel.toDate = { ...this.dateToday };
    this.formModel.fromTime = { hour: fromDate.getHours(), minute: fromDate.getMinutes() };
    this.formModel.toTime = { hour: now.getHours(), minute: now.getMinutes() };
  }

  getUsageStatistics(): void {
    this.hasQueried = true;
    this.isLoading = true;
    const timestampFrom = this.timezoneService.resolveLocalDateTime(
      this.formModel.fromDate, this.formModel.fromTime, this.timezone);
    const timestampUntil = this.timezoneService.resolveLocalDateTime(
      this.formModel.toDate, this.formModel.toTime, this.timezone);
    this.usageStatisticsService.getUsageStatistics(
      timestampFrom, timestampUntil,
    ).subscribe({
      next: (statsRange: UsageStatisticsRange) => {
        this.timeRange = {
          startTime: timestampFrom,
          endTime: timestampUntil,
        };
        this.fetchedData = statsRange.result;
        this.drawLineChart();
        this.isLoading = false;
      },
      error: (e: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(e.error.message);
        this.isLoading = false;
      },
    });
  }

  changeStatsType(type: StatisticsType): void {
    this.formModel.dataType = type;
    this.drawLineChart();
  }

  changeAggregationType(type: AggregationType): void {
    this.formModel.aggregationType = type;
    this.drawLineChart();
  }

  drawLineChart(): void {
    if (!this.fetchedData.length) {
      return;
    }
    if (+this.formModel.aggregationType === AggregationType.DAILY && this.fetchedData.length < 24 * 7) {
      // Do not allow daily aggregation if there are too few data, e.g. less than one week
      this.statusMessageService.showWarningToast('There is too little data to be aggregated daily.');
      this.formModel.aggregationType = AggregationType.HOURLY;
    } else if (+this.formModel.aggregationType === AggregationType.HOURLY && this.fetchedData.length > 30 * 24) {
      // Do not allow hourly aggregation if there are too many data, e.g. more than one month
      this.statusMessageService.showWarningToast('There is too many data to be aggregated hourly.');
      this.formModel.aggregationType = AggregationType.DAILY;
    }
    const aggregateDaily = +this.formModel.aggregationType === AggregationType.DAILY;
    let dataToDraw = this.fetchedData.map((statisticsObj: UsageStatistics) => {
      let value: number;
      switch (+this.formModel.dataType) {
        case StatisticsType.NUM_RESPONSES:
          value = statisticsObj.numResponses;
          this.itemName = 'responses';
          break;
        case StatisticsType.NUM_COURSES:
          value = statisticsObj.numCourses;
          this.itemName = 'courses';
          break;
        case StatisticsType.NUM_STUDENTS:
          value = statisticsObj.numStudents;
          this.itemName = 'students';
          break;
        case StatisticsType.NUM_INSTRUCTORS:
          value = statisticsObj.numInstructors;
          this.itemName = 'instructors';
          break;
        case StatisticsType.NUM_ACCOUNT_REQUESTS:
          value = statisticsObj.numAccountRequests;
          this.itemName = 'account requests';
          break;
        case StatisticsType.NUM_EMAILS:
          value = statisticsObj.numEmails;
          this.itemName = 'emails sent';
          break;
        case StatisticsType.NUM_SUBMISSIONS:
          value = statisticsObj.numSubmissions;
          this.itemName = 'submissions';
          break;
        default:
          throw new Error('Unexpected statsType');
      }
      return {
        value,
        date: new Date(statisticsObj.startTime).toISOString(),
      };
    });

    if (aggregateDaily) {
      const aggregated: DataPoint[] = [];
      let current: DataPoint = {
        value: 0,
        date: dataToDraw[0].date,
      };
      for (const hourlyData of dataToDraw) {
        const shouldChangeDate = new Date(hourlyData.date).getUTCHours() === 0;
        if (shouldChangeDate) {
          aggregated.push(current);
          current = {
            value: hourlyData.value,
            date: hourlyData.date,
          };
        } else {
          current.value += hourlyData.value;
        }
      }
      aggregated.push(current);
      dataToDraw = aggregated;
    }

    this.dataToDraw = dataToDraw;
  }

}
