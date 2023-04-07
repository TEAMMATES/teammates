import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DateFormat, TimeFormat, getDefaultTimeFormat, getDefaultDateFormat } from '../../../types/datetime-const';

/**
 * Time picker with fixed time to pick.
 */
@Component({
  selector: 'tm-timepicker',
  templateUrl: './timepicker.component.html',
  styleUrls: ['./timepicker.component.scss'],
})
export class TimepickerComponent {
  @Input()
  isDisabled: boolean = false;

  @Input()
  time: TimeFormat = getDefaultTimeFormat();

  @Input()
  minTime?: TimeFormat;

  @Input()
  maxTime?: TimeFormat;

  @Input()
  date: DateFormat = getDefaultDateFormat();

  @Input()
  minDate?: DateFormat;

  @Input()
  maxDate?: DateFormat;

  @Output()
  timeChange: EventEmitter<TimeFormat> = new EventEmitter();

  /**
   * Triggers time change event.
   */
  triggerTimeChange(newTime: TimeFormat): void {
    this.timeChange.emit(newTime);
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
    return t1 && t2 && t1.hour === t2.hour && t1.minute === t2.minute;
  }

  /**
   * Checks whether the time option should be disabled when a minimum datetime and/or a maximum datetime is/are
   * specified.
   *
   * <p> The valid time option is greater or equal than the minimum datetime and smaller or equal than the maximum
   * datetime.
   */
  isOptionDisabled(t: TimeFormat): boolean {
    const date = this.toJsDate(this.date, t);

    if (this.minDate && this.minTime) {
      const minDate = this.toJsDate(this.minDate, this.minTime);
      if (date < minDate) {
        return true;
      }
    }

    if (this.maxDate && this.maxTime) {
      const maxDate = this.toJsDate(this.maxDate, this.maxTime);
      if (date > maxDate) {
        return true;
      }
    }

    return false;
  }

  private toJsDate(date: DateFormat, time: TimeFormat): Date {
    return new Date(
      date.year,
      date.month - 1,
      date.day,
      time.hour,
      time.minute,
    );
  }

  /**
   * Checks whether the time is in the fixed list to select.
   */
  isInFixedList(time: TimeFormat): boolean {
    return (time.hour >= 1 && time.hour <= 23 && time.minute === 0)
        || (time.hour === 23 && time.minute === 59);
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
