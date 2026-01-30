import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';




import { StudentListModule } from '../../components/student-list/student-list.module';



const routes: Routes = [
  {
    path: '',
    component: InstructorCourseDetailsPageComponent,
  },
];

/**
 * Module for instructor course details page.
 */
@NgModule({
  exports: [
    InstructorCourseDetailsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    StudentListModule,
    InstructorCourseDetailsPageComponent,
],
})
export class InstructorCourseDetailsPageModule { }
