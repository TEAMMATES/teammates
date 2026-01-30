import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TimepickerComponent } from './timepicker.component';

/**
 * Timepicker module.
 */
@NgModule({
  exports: [TimepickerComponent],
  imports: [
    CommonModule,
    FormsModule,
    TimepickerComponent,
  ],
})
export class TimepickerModule { }
