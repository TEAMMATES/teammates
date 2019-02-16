import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';

/**
 * Module for instructor course edit page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    InstructorCourseEditPageComponent,
  ],
  exports: [
    InstructorCourseEditPageComponent,
  ],
  entryComponents: [],
})
export class InstructorCourseEditPageModule { }
