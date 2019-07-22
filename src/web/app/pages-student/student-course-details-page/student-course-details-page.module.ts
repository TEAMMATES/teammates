import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentCourseDetailsPageComponent } from './student-course-details-page.component';

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
    RouterModule,
    TeammatesCommonModule,
  ],
})
export class StudentCourseDetailsPageModule { }
