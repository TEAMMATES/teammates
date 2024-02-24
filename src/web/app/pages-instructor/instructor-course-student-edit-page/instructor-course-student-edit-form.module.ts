import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { InstructorCourseStudentEditPageComponent } from './instructor-course-student-edit-page.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

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
    LoadingSpinnerModule,
    LoadingRetryModule,
  ],
})
export class InstructorCourseStudentEditFormModule { }
