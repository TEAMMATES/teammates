import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
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
  declarations: [
    InstructorCourseStudentEditPageComponent,
  ],
  exports: [
    InstructorCourseStudentEditPageComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
  ],
})
export class InstructorCourseStudentEditPageModule { }
