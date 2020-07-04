import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ClipboardModule } from 'ngx-clipboard';
import { AjaxPreloadModule } from '../../components/ajax-preload/ajax-preload.module';
import { StudentListModule } from '../../components/student-list/student-list.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';

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
    ClipboardModule,
    RouterModule,
    StudentListModule,
    AjaxPreloadModule,
  ],
})
export class InstructorCourseDetailsPageModule { }
