import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstructorCourseStudentEditFormModule } from './instructor-course-student-edit-form.module';
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
    InstructorCourseStudentEditFormModule,
    RouterModule.forChild(routes),
  ],
})
export class InstructorCourseStudentEditPageModule { }
