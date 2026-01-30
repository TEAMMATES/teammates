import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { InstructorCourseStudentEditPageComponent } from './instructor-course-student-edit-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorCourseStudentEditPageComponent,
  },
];

/**
 * Module for instructor course student edit page.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
],
})
export class InstructorCourseStudentEditPageModule { }
