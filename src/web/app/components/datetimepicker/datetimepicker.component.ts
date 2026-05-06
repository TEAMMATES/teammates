import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DateFormat, TimeFormat, getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';
import { DatepickerComponent } from '../datepicker/datepicker.component';
import { TimepickerComponent } from '../timepicker/timepicker.component';

/**
 * DateTime picker component that wraps Datepicker and Timepicker components.
 */
@Component({
  selector: 'tm-datetimepicker',
  templateUrl: './datetimepicker.component.html',
  imports: [DatepickerComponent, TimepickerComponent],
})
export class DatetimepickerComponent {
  @Input()
  dateTime: Date | undefined;

  @Input()
  isDisabled = false;

  @Input()
  minDateTime?: Date;

  @Input()
  maxDateTime?: Date;

  @Output()
  dateTimeChange: EventEmitter<Date> = new EventEmitter<Date>();

  get minDate(): DateFormat | undefined {
    return this.minDateTime
      ? {
          year: this.minDateTime.getFullYear(),
          month: this.minDateTime.getMonth() + 1,
          day: this.minDateTime.getDate(),
        }
      : undefined;
  }

  get maxDate(): DateFormat | undefined {
    return this.maxDateTime
      ? {
          year: this.maxDateTime.getFullYear(),
          month: this.maxDateTime.getMonth() + 1,
          day: this.maxDateTime.getDate(),
        }
      : undefined;
  }

  get minTime(): TimeFormat | undefined {
    return this.minDateTime
      ? {
          hour: this.minDateTime.getHours(),
          minute: this.minDateTime.getMinutes(),
        }
      : undefined;
  }

  get maxTime(): TimeFormat | undefined {
    return this.maxDateTime
      ? {
          hour: this.maxDateTime.getHours(),
          minute: this.maxDateTime.getMinutes(),
        }
      : undefined;
  }

  get date(): DateFormat {
    if (this.dateTime) {
      return {
        year: this.dateTime.getFullYear(),
        month: this.dateTime.getMonth() + 1,
        day: this.dateTime.getDate(),
      };
    }
    return getDefaultDateFormat();
  }

  get time(): TimeFormat {
    if (this.dateTime) {
      return {
        hour: this.dateTime.getHours(),
        minute: this.dateTime.getMinutes(),
      };
    }
    return getDefaultTimeFormat();
  }

  onDateChange(newDate: DateFormat): void {
    const time = this.time;
    this.dateTime = new Date(newDate.year, newDate.month - 1, newDate.day, time.hour, time.minute);
    this.dateTimeChange.emit(this.dateTime);
  }

  onTimeChange(newTime: TimeFormat): void {
    const date = this.date;
    this.dateTime = new Date(date.year, date.month - 1, date.day, newTime.hour, newTime.minute);
    this.dateTimeChange.emit(this.dateTime);
  }
}
