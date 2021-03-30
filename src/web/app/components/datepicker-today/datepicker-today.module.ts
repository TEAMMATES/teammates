import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { DatepickerTodayComponent } from './datepicker-today.component';

/**
 * Datepicker with today button module.
 */
@NgModule({
  declarations: [DatepickerTodayComponent],
  imports: [
    CommonModule,
    FormsModule,
    NgbDatepickerModule,
  ],
  exports: [
    DatepickerTodayComponent,
  ],
})
export class DatepickerTodayModule { }
