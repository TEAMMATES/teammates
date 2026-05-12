import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbCalendar, NgbDateStruct, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { DateFormat } from '../../../types/datetime-const';

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
  calendar = inject(NgbCalendar);

  @Input()
  date: DateFormat | undefined;

  @Input()
  isDisabled = false;

  @Input()
  maxDate?: NgbDateStruct;

  @Input()
  minDate?: NgbDateStruct;

  @Output()
  dateChangeCallback: EventEmitter<DateFormat> = new EventEmitter<DateFormat>();

  changeDate(date: DateFormat): void {
    this.dateChangeCallback.emit(date);
  }

  selectTodayDate(dp: NgbInputDatepicker): void {
    this.dateChangeCallback.emit(this.calendar.getToday());
    dp.navigateTo(this.calendar.getToday());
  }
}
