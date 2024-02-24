import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentCourseDetailsPageComponent } from './student-course-details-page.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';

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
  declarations: [
    StudentCourseDetailsPageComponent,
  ],
  exports: [
    StudentCourseDetailsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TeammatesCommonModule,
    LoadingSpinnerModule,
    LoadingRetryModule,
  ],
})
export class StudentCourseDetailsPageModule { }
