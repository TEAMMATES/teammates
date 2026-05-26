import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { DateTimeService } from '../../../services/datetime.service';
import { DateFormat, TimeFormat, getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';
import { DatepickerComponent } from '../datepicker/datepicker.component';
import { TimepickerComponent } from '../timepicker/timepicker.component';

/**
 * Date and time picker component that combines date and time values into a single Date.
 */
@Component({
  selector: 'tm-datetimepicker',
  templateUrl: './datetimepicker.component.html',
  imports: [DatepickerComponent, TimepickerComponent],
})
export class DatetimepickerComponent {
  private datetimeService = inject(DateTimeService);

  @Input()
  dateTime?: Date;

  @Input()
  isDisabled = false;

  @Input()
  minDateTime?: Date;

  @Input()
  maxDateTime?: Date;

  @Output()
  dateTimeChange: EventEmitter<Date> = new EventEmitter<Date>();

  get date(): DateFormat {
    if (!this.dateTime) {
      return getDefaultDateFormat();
    }
    const [date] = this.datetimeService.convertDateToDateFormatAndTimeFormat(this.dateTime);
    return date;
  }

  get time(): TimeFormat {
    if (!this.dateTime) {
      return getDefaultTimeFormat();
    }
    const [, time] = this.datetimeService.convertDateToDateFormatAndTimeFormat(this.dateTime);
    return time;
  }

  get minDate(): DateFormat | undefined {
    return this.getDateFormat(this.minDateTime);
  }

  get minTime(): TimeFormat | undefined {
    return this.getTimeFormat(this.minDateTime);
  }

  get maxDate(): DateFormat | undefined {
    return this.getDateFormat(this.maxDateTime);
  }

  get maxTime(): TimeFormat | undefined {
    return this.getTimeFormat(this.maxDateTime);
  }

  onDateChange(newDate: DateFormat): void {
    this.updateDateTime(newDate, this.time);
  }

  onTimeChange(newTime: TimeFormat): void {
    this.updateDateTime(this.date, newTime);
  }

  private updateDateTime(date: DateFormat, time: TimeFormat): void {
    this.dateTime = this.datetimeService.convertDateFormatAndTimeFormatToDate(date, time);
    this.dateTimeChange.emit(this.dateTime);
  }

  private getDateFormat(dateTime?: Date): DateFormat | undefined {
    if (!dateTime) {
      return undefined;
    }
    const [date] = this.datetimeService.convertDateToDateFormatAndTimeFormat(dateTime);
    return date;
  }

  private getTimeFormat(dateTime?: Date): TimeFormat | undefined {
    if (!dateTime) {
      return undefined;
    }
    const [, time] = this.datetimeService.convertDateToDateFormatAndTimeFormat(dateTime);
    return time;
  }
}
