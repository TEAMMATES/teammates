import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
  exports: [
    InstructorCourseStudentDetailsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    InstructorCourseStudentDetailsPageComponent,
],
})
export class InstructorCourseStudentDetailsPageModule { }
