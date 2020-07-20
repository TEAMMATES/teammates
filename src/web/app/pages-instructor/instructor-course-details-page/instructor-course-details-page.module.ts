import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { AjaxPreloadModule } from '../../components/ajax-preload/ajax-preload.module';
import { StudentListModule } from '../../components/student-list/student-list.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';

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
  declarations: [
    InstructorCourseDetailsPageComponent,
  ],
  exports: [
    InstructorCourseDetailsPageComponent,
  ],
  imports: [
    CommonModule,
    TeammatesCommonModule,
    RouterModule.forChild(routes),
    StudentListModule,
    AjaxLoadingModule,
    AjaxPreloadModule,
  ],
})
export class InstructorCourseDetailsPageModule { }
