import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AddCourseFormComponent } from './add-course-form.component';

/**
 * Module for form for adding courses.
 */
@NgModule({
  declarations: [AddCourseFormComponent],
  exports: [AddCourseFormComponent],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
  ],
})
export class AddCourseFormModule { }
