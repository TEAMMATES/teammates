import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TimepickerComponent } from './timepicker.component';

/**
 * Timepicker module.
 */
@NgModule({
  declarations: [TimepickerComponent],
  exports: [TimepickerComponent],
  imports: [
    CommonModule,
    FormsModule,
  ],
})
export class TimepickerModule { }
