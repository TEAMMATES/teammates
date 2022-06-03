import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DateFormat } from '../datepicker/datepicker.component';

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
  time: TimeFormat = { hour: 0, minute: 0 };

  @Input()
  minTime: TimeFormat | undefined;

  @Input()
  maxTime: TimeFormat | undefined;

  @Input()
  date: DateFormat = { year: 0, month: 0, day: 0 };

  @Input()
  minDate: DateFormat | undefined;

  @Input()
  maxDate: DateFormat | undefined;

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

  isOptionDisabled(t: TimeFormat): boolean {
    if (this.minTime) {
      return this.date.year === this.minDate?.year && this.date.month === this.minDate?.month
          && this.date.day === this.minDate?.day && t.hour < this.minTime?.hour;
    }
    if (this.maxTime) {
      return this.date.year === this.maxDate?.year && this.date.month === this.maxDate?.month
          && this.date.day === this.maxDate?.day && t.hour > this.maxTime?.hour;
    }
    return false;
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

/**
 * The output format of the time picker.
 */
export interface TimeFormat {
  hour: number;
  minute: number;
}
