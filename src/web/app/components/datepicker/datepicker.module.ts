import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { DatepickerComponent } from './datepicker.component';

/**
 * Datepicker with today button module.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbDatepickerModule,
    DatepickerComponent,
  ],
  exports: [
    DatepickerComponent,
  ],
})
export class DatepickerModule { }
