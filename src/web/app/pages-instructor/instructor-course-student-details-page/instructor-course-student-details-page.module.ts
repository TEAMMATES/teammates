import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentProfileModule } from '../student-profile/student-profile.module';
import { InstructorCourseStudentDetailsPageComponent } from './instructor-course-student-details-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorCourseStudentDetailsPageComponent,
  },
];

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
    RouterModule.forChild(routes),
  ],
})
export class InstructorCourseStudentDetailsPageModule { }
