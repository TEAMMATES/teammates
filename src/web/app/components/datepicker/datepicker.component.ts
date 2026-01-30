import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbCalendar, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { DateFormat } from '../../../types/datetime-const';
import { FormsModule } from '@angular/forms';

/**
 * Datepicker with today button component
 */
@Component({
  selector: 'tm-datepicker',
  templateUrl: './datepicker.component.html',
  styleUrls: ['./datepicker.component.scss'],
  imports: [NgbInputDatepicker, FormsModule],
})
export class DatepickerComponent {

  @Input()
  date: DateFormat | undefined;

  @Input()
  isDisabled: boolean = false;

  @Input()
  maxDate: DateFormat | undefined;

  @Input()
  minDate: DateFormat | undefined;

  @Output()
  dateChangeCallback: EventEmitter<DateFormat> = new EventEmitter<DateFormat>();

  constructor(public calendar: NgbCalendar) { }

  changeDate(date: DateFormat): void {
    this.dateChangeCallback.emit(date);
  }

  selectTodayDate(dp: NgbInputDatepicker): void {
    this.dateChangeCallback.emit(this.calendar.getToday());
    dp.navigateTo(this.calendar.getToday());
  }

}
