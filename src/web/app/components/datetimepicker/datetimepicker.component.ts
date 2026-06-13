import { Component, EventEmitter, Input, OnChanges, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbCalendar, NgbDateParserFormatter, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { DatePickerFormatter } from './datepicker-formatter';
import { DateTimeService } from '../../../services/datetime.service';
import { TimezoneService } from '../../../services/timezone.service';
import { DateFormat, TimeFormat, getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';

/**
 * The datetimepicker always resolves midnight to 23:59 of the previous day.
 */
const RESOLVE_MIDNIGHT = true;

/**
 * Combined date and time picker.
 */
@Component({
  selector: 'tm-datetimepicker',
  templateUrl: './datetimepicker.component.html',
  styleUrls: ['./datetimepicker.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: DatePickerFormatter }],
  imports: [NgbInputDatepicker, FormsModule],
})
export class DatetimepickerComponent implements OnChanges {
  private readonly dateTimeService = inject(DateTimeService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly calendar = inject(NgbCalendar);

  /**
   * The selected value as a UNIX millisecond timestamp. Undefined renders an empty picker.
   */
  @Input()
  timestamp?: number;

  /**
   * Timezone used to interpret the timestamps. Falls back to the browser's guessed timezone.
   */
  @Input()
  timeZone = '';

  /**
   * Lower bound of the selectable range as a UNIX millisecond timestamp.
   */
  @Input()
  minTimestamp?: number;

  /**
   * Upper bound of the selectable range as a UNIX millisecond timestamp.
   */
  @Input()
  maxTimestamp?: number;

  @Input()
  isDisabled = false;

  @Output()
  timestampChange: EventEmitter<number> = new EventEmitter<number>();

  // Internal display state derived from the timestamp inputs.
  date: DateFormat = getDefaultDateFormat();
  time: TimeFormat = getDefaultTimeFormat();
  minDate?: DateFormat;
  maxDate?: DateFormat;
  minTime?: TimeFormat;
  maxTime?: TimeFormat;

  ngOnChanges(): void {
    if (this.timestamp) {
      const { date, time } = this.dateTimeService.getDateTimeAtTimezone(
        this.timestamp,
        this.effectiveTimeZone,
        RESOLVE_MIDNIGHT,
      );
      this.date = date;
      this.time = time;
    }

    const min = this.toDateTime(this.minTimestamp);
    this.minDate = min?.date;
    this.minTime = min?.time;

    const max = this.toDateTime(this.maxTimestamp);
    this.maxDate = max?.date;
    this.maxTime = max?.time;
  }

  private get effectiveTimeZone(): string {
    return this.timeZone || this.timezoneService.guessTimezone();
  }

  private toDateTime(timestamp?: number): { date: DateFormat; time: TimeFormat } | undefined {
    if (!timestamp) {
      return undefined;
    }
    return this.dateTimeService.getDateTimeAtTimezone(timestamp, this.effectiveTimeZone, RESOLVE_MIDNIGHT);
  }

  changeDate(date: DateFormat): void {
    this.date = date;
    this.emitTimestamp();
  }

  changeTime(time: TimeFormat): void {
    this.time = time;
    this.emitTimestamp();
  }

  selectTodayDate(dp: NgbInputDatepicker): void {
    const today = this.calendar.getToday();
    this.date = { year: today.year, month: today.month, day: today.day };
    dp.navigateTo(today);
    this.emitTimestamp();
  }

  private emitTimestamp(): void {
    if (!this.date) {
      return;
    }
    this.timestampChange.emit(
      this.timezoneService.resolveLocalDateTime(this.date, this.time, this.effectiveTimeZone, RESOLVE_MIDNIGHT),
    );
  }

  /**
   * Helper function to create a range.
   */
  range(start: number, end: number): number[] {
    const arr: number[] = [];
    for (let i: number = start; i < end; i += 1) {
      arr.push(i);
    }
    return arr;
  }

  /**
   * Compares two TIMEs.
   *
   * <p>Checks whether they are equal or not.
   */
  timeCompareFn(t1: TimeFormat, t2: TimeFormat): boolean {
    // eslint-disable-next-line @typescript-eslint/prefer-optional-chain
    return t1 && t2 && t1.hour === t2.hour && t1.minute === t2.minute;
  }

  /**
   * Checks whether the time option should be disabled given the selected date and the min/max bounds.
   *
   * <p> The valid time option is greater or equal than the minimum datetime and smaller or equal than the
   * maximum datetime.
   */
  isOptionDisabled(t: TimeFormat): boolean {
    if (!this.date) {
      return false;
    }
    const date = this.toJsDate(this.date, t);

    if (this.minDate && this.minTime) {
      if (date < this.toJsDate(this.minDate, this.minTime)) {
        return true;
      }
    }

    if (this.maxDate && this.maxTime) {
      if (date > this.toJsDate(this.maxDate, this.maxTime)) {
        return true;
      }
    }

    return false;
  }

  private toJsDate(date: DateFormat, time: TimeFormat): Date {
    return new Date(date.year, date.month - 1, date.day, time.hour, time.minute);
  }

  /**
   * Checks whether the time is in the fixed list to select.
   */
  isInFixedList(time: TimeFormat): boolean {
    return (time.hour >= 1 && time.hour <= 23 && time.minute === 0) || (time.hour === 23 && time.minute === 59);
  }

  /**
   * Formats number {@code i} and pads leading zeros if its digits are less than {@code n}.
   *
   * <p>e.g. n = 2, i = 1 => "01"
   */
  addLeadingZeros(n: number, i: number): string {
    return ('0'.repeat(n) + i).slice(-n);
  }
}
