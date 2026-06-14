import { Component, EventEmitter, Input, OnChanges, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDateParserFormatter, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import moment from 'moment-timezone';
import { DatePickerFormatter } from './datepicker-formatter';
import { DateTimeService } from '../../../services/datetime.service';
import { TimezoneService } from '../../../services/timezone.service';
import { DateFormat, TimeFormat, getDefaultTimeFormat } from '../../../types/datetime-const';

/**
 * Combined date and time picker.
 *
 * The empty state is represented by an undefined timestamp. Any defined number is treated as a real
 * instant.
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
  date?: DateFormat;
  time: TimeFormat = getDefaultTimeFormat();
  minDate?: DateFormat;
  maxDate?: DateFormat;
  minTime?: TimeFormat;
  maxTime?: TimeFormat;

  ngOnChanges(): void {
    if (this.timestamp == null) {
      this.date = undefined;
      this.time = getDefaultTimeFormat();
    } else {
      const { date, time } = this.toDisplayDateTime(this.timestamp);
      this.date = date;
      this.time = time;
    }

    const min = this.minTimestamp == null ? undefined : this.toDisplayDateTime(this.minTimestamp);
    this.minDate = min?.date;
    this.minTime = min?.time;

    const max = this.maxTimestamp == null ? undefined : this.toDisplayDateTime(this.maxTimestamp);
    this.maxDate = max?.date;
    this.maxTime = max?.time;
  }

  private get effectiveTimeZone(): string {
    return this.timeZone || this.timezoneService.guessTimezone();
  }

  /**
   * Converts an instant into the date and time shown in the picker.
   * A midnight instant is shown as 23:59 of the previous day.
   */
  private toDisplayDateTime(timestamp: number): { date: DateFormat; time: TimeFormat } {
    return this.dateTimeService.getDateTimeAtTimezone(timestamp, this.effectiveTimeZone, true);
  }

  /**
   * Converts the displayed date and time back into an instant.
   * The inverse of {@link toDisplayDateTime}: a displayed 23:59 is resolved to the following midnight.
   */
  private fromDisplayDateTime(date: DateFormat, time: TimeFormat): number {
    return this.timezoneService.resolveLocalDateTime(date, time, this.effectiveTimeZone, true);
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
    const m = moment().tz(this.effectiveTimeZone);
    const today: DateFormat = { year: m.year(), month: m.month() + 1, day: m.date() };
    this.date = today;
    dp.navigateTo(today);
    this.emitTimestamp();
  }

  private emitTimestamp(): void {
    if (this.date == null) {
      return;
    }
    this.timestampChange.emit(this.clampToRange(this.fromDisplayDateTime(this.date, this.time)));
  }

  /**
   * Snaps a value outside the allowed range to the nearest selectable boundary.
   */
  private clampToRange(timestamp: number): number {
    if (this.minTimestamp != null && timestamp < this.minTimestamp) {
      return this.ceilToSelectableTime(this.minTimestamp);
    }
    if (this.maxTimestamp != null && timestamp > this.maxTimestamp) {
      return this.floorToSelectableTime(this.maxTimestamp);
    }
    return timestamp;
  }

  /**
   * Rounds a timestamp up to the next selectable time option (a whole hour). A time in the last hour of the
   * day rounds up to the following midnight, which is displayed as 23:59.
   */
  private ceilToSelectableTime(timestamp: number): number {
    const inst: moment.Moment = moment(timestamp).tz(this.effectiveTimeZone).second(0).millisecond(0);
    if (inst.minute() > 0) {
      inst.add(1, 'hour').minute(0);
    }
    return inst.valueOf();
  }

  /**
   * Rounds a timestamp down to the previous selectable time option (a whole hour).
   */
  private floorToSelectableTime(timestamp: number): number {
    const inst: moment.Moment = moment(timestamp).tz(this.effectiveTimeZone).second(0).millisecond(0);
    if (inst.minute() > 0) {
      inst.minute(0);
    }
    return inst.valueOf();
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
   * Checks whether they are equal or not.
   */
  timeCompareFn(t1: TimeFormat, t2: TimeFormat): boolean {
    // eslint-disable-next-line @typescript-eslint/prefer-optional-chain
    return t1 && t2 && t1.hour === t2.hour && t1.minute === t2.minute;
  }

  /**
   * Checks whether the time option should be disabled given the selected date and the min/max bounds.
   *
   * The valid time option is greater or equal than the minimum datetime and smaller or equal than the
   * maximum datetime.
   */
  isOptionDisabled(t: TimeFormat): boolean {
    if (this.date == null) {
      return false;
    }
    const candidate = this.fromDisplayDateTime(this.date, t);

    if (this.minTimestamp != null && candidate < this.minTimestamp) {
      return true;
    }

    if (this.maxTimestamp != null && candidate > this.maxTimestamp) {
      return true;
    }

    return false;
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
   * e.g. n = 2, i = 1 => "01"
   */
  addLeadingZeros(n: number, i: number): string {
    return ('0'.repeat(n) + i).slice(-n);
  }
}
