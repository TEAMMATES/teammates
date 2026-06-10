import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { DateFormat, TimeFormat, getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';
import { DatepickerComponent } from '../datepicker/datepicker.component';
import { TimepickerComponent } from '../timepicker/timepicker.component';

/**
 * Combined date and time picker component.
 *
 * <p>Wraps {@link DatepickerComponent} and {@link TimepickerComponent} into a single
 * component so that consumers have one consistent place to manage date+time input
 * without having to wire the two pickers together themselves.
 */
@Component({
  selector: 'tm-datetimepicker',
  templateUrl: './datetimepicker.component.html',
  imports: [DatepickerComponent, TimepickerComponent],
})
export class DatetimepickerComponent {
  @Input()
  isDisabled = false;

  @Input()
  date: DateFormat = getDefaultDateFormat();

  @Input()
  time: TimeFormat = getDefaultTimeFormat();

  @Input()
  minDate?: NgbDateStruct;

  @Input()
  maxDate?: NgbDateStruct;

  @Input()
  minDateTime?: DateFormat;

  @Input()
  maxDateTime?: DateFormat;

  @Input()
  minTime?: TimeFormat;

  @Input()
  maxTime?: TimeFormat;

  @Output()
  dateChangeCallback: EventEmitter<DateFormat> = new EventEmitter<DateFormat>();

  @Output()
  timeChange: EventEmitter<TimeFormat> = new EventEmitter<TimeFormat>();

  onDateChange(newDate: DateFormat): void {
    this.dateChangeCallback.emit(newDate);
  }

  onTimeChange(newTime: TimeFormat): void {
    this.timeChange.emit(newTime);
  }
}
