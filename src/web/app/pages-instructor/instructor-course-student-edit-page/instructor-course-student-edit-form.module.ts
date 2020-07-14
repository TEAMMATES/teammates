import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { InstructorCourseStudentEditPageComponent } from './instructor-course-student-edit-page.component';

/**
 * Module for course student edit form.
 */
@NgModule({
  declarations: [
    InstructorCourseStudentEditPageComponent,
  ],
  exports: [
    InstructorCourseStudentEditPageComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
  ],
})
export class InstructorCourseStudentEditFormModule { }
