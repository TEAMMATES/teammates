import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NewInstructorDataRowComponent } from './new-instructor-data-row.component';

/**
 * Module for rows of new instructor data.
 */
@NgModule({
  declarations: [NewInstructorDataRowComponent],
  exports: [
    NewInstructorDataRowComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
})
export class NewInstructorDataRowModule { }
