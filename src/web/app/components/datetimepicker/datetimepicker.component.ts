import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DateFormat, TimeFormat } from '../../../types/datetime-const';
import { DatepickerComponent } from '../datepicker/datepicker.component';
import { TimepickerComponent } from '../timepicker/timepicker.component';

/**
 * DateTime picker component that wraps Datepicker and Timepicker components.
 */
@Component({
  selector: 'tm-datetimepicker',
  templateUrl: './datetimepicker.component.html',
  styleUrls: ['./datetimepicker.component.scss'],
  imports: [DatepickerComponent, TimepickerComponent, NgClass],
})
export class DatetimepickerComponent {
  @Input()
  dateId = '';

  @Input()
  timeId = '';

  @Input()
  dateColClass = 'col-md-7 col-xs-center';

  @Input()
  timeColClass = 'col-md-5';

  @Input()
  date: DateFormat | undefined;

  @Input()
  time: TimeFormat | undefined;

  @Input()
  isDisabled = false;

  @Input()
  minDate?: DateFormat;

  @Input()
  maxDate?: DateFormat;

  @Input()
  minTime?: TimeFormat;

  @Input()
  maxTime?: TimeFormat;

  @Output()
  dateChange: EventEmitter<DateFormat> = new EventEmitter<DateFormat>();

  @Output()
  timeChange: EventEmitter<TimeFormat> = new EventEmitter<TimeFormat>();

  @Output()
  dateTimeChange: EventEmitter<{ date: DateFormat; time: TimeFormat }> = new EventEmitter();

  onDateChange(newDate: DateFormat): void {
    this.date = newDate;
    this.dateChange.emit(newDate);
    if (this.time) {
      this.dateTimeChange.emit({ date: newDate, time: this.time });
    }
  }

  onTimeChange(newTime: TimeFormat): void {
    this.time = newTime;
    this.timeChange.emit(newTime);
    if (this.date) {
      this.dateTimeChange.emit({ date: this.date, time: newTime });
    }
  }
}
