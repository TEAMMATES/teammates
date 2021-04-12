import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbCalendar, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { DateFormat } from '../session-edit-form/session-edit-form-model';

/**
 * Datepicker with today button component
 */
@Component({
  selector: 'tm-datepicker',
  templateUrl: './datepicker.component.html',
  styleUrls: ['./datepicker.component.scss'],
})
export class DatepickerComponent implements OnInit {

  @Input()
  date: DateFormat | undefined;

  @Input()
  disabled: boolean = false;

  @Input()
  maxDate: DateFormat | undefined;

  @Input()
  minDate: DateFormat | undefined;

  @Output()
  dateChangeCallback: EventEmitter<DateFormat> = new EventEmitter<DateFormat>();

  constructor(public calendar: NgbCalendar) { }

  ngOnInit(): void {
  }

  changeDate(date: DateFormat): void {
    this.dateChangeCallback.emit(date);
  }

  selectTodayDate(dp: NgbInputDatepicker): void {
    this.dateChangeCallback.emit(this.calendar.getToday());
    dp.navigateTo(this.calendar.getToday());
  }

}
