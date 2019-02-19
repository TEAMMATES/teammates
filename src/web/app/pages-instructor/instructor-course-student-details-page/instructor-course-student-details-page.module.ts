import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StudentProfileModule } from '../student-profile/student-profile.module';
import { InstructorCourseStudentDetailsPageComponent } from './instructor-course-student-details-page.component';

/**
 * Module for instructor course student details page.
 */
@NgModule({
  declarations: [
    InstructorCourseStudentDetailsPageComponent,
  ],
  exports: [
    InstructorCourseStudentDetailsPageComponent,
  ],
  imports: [
    CommonModule,
    StudentProfileModule,
    RouterModule,
  ],
})
export class InstructorCourseStudentDetailsPageModule { }
