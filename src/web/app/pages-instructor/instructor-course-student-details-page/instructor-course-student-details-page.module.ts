import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstructorCourseStudentDetailsPageComponent } from './instructor-course-student-details-page.component';
import { CourseRelatedInfoModule } from '../../components/course-related-info/course-related-info.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

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
    CourseRelatedInfoModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    LoadingRetryModule,
    TeammatesRouterModule,
  ],
})
export class InstructorCourseStudentDetailsPageModule { }
