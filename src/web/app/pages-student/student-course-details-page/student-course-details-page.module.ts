import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentCourseDetailsPageComponent } from './student-course-details-page.component';




const routes: Routes = [
  {
    path: '',
    component: StudentCourseDetailsPageComponent,
  },
];

/**
 * Module for student course details page.
 */
@NgModule({
  exports: [
    StudentCourseDetailsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    StudentCourseDetailsPageComponent,
],
})
export class StudentCourseDetailsPageModule { }
