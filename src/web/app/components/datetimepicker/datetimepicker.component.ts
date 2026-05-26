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

  private internalDateTime?: Date;
  private internalMinDateTime?: Date;
  private internalMaxDateTime?: Date;

  @Input()
  set dateTime(dateTime: Date | undefined) {
    this.internalDateTime = dateTime;
    [this.date, this.time] = this.getDateTimeFormatsWithDefaults(dateTime);
  }

  get dateTime(): Date | undefined {
    return this.internalDateTime;
  }

  @Input()
  isDisabled = false;

  @Input()
  set minDateTime(minDateTime: Date | undefined) {
    this.internalMinDateTime = minDateTime;
    [this.minDate, this.minTime] = this.getOptionalDateTimeFormats(minDateTime);
  }

  get minDateTime(): Date | undefined {
    return this.internalMinDateTime;
  }

  @Input()
  set maxDateTime(maxDateTime: Date | undefined) {
    this.internalMaxDateTime = maxDateTime;
    [this.maxDate, this.maxTime] = this.getOptionalDateTimeFormats(maxDateTime);
  }

  get maxDateTime(): Date | undefined {
    return this.internalMaxDateTime;
  }

  @Output()
  dateTimeChange: EventEmitter<Date> = new EventEmitter<Date>();

  date: DateFormat = getDefaultDateFormat();
  time: TimeFormat = getDefaultTimeFormat();
  minDate?: DateFormat;
  minTime?: TimeFormat;
  maxDate?: DateFormat;
  maxTime?: TimeFormat;

  onDateChange(newDate: DateFormat): void {
    this.updateDateTime(newDate, this.time);
  }

  onTimeChange(newTime: TimeFormat): void {
    this.updateDateTime(this.date, newTime);
  }

  private updateDateTime(date: DateFormat, time: TimeFormat): void {
    const updatedDateTime = this.datetimeService.convertDateFormatAndTimeFormatToDate(date, time);
    if (this.internalDateTime?.getTime() === updatedDateTime.getTime()) {
      return;
    }

    this.date = date;
    this.time = time;
    this.internalDateTime = updatedDateTime;
    this.dateTimeChange.emit(updatedDateTime);
  }

  private getDateTimeFormatsWithDefaults(dateTime?: Date): [DateFormat, TimeFormat] {
    if (!dateTime) {
      return [getDefaultDateFormat(), getDefaultTimeFormat()];
    }
    return this.datetimeService.convertDateToDateFormatAndTimeFormat(dateTime);
  }

  private getOptionalDateTimeFormats(dateTime?: Date): [DateFormat | undefined, TimeFormat | undefined] {
    if (!dateTime) {
      return [undefined, undefined];
    }
    return this.datetimeService.convertDateToDateFormatAndTimeFormat(dateTime);
  }
}
