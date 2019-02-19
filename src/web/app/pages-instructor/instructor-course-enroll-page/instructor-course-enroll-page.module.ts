import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { HotTableModule } from '@handsontable/angular';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxPreloadModule } from '../../components/ajax-preload/ajax-preload.module';
import { StatusMessageModule } from '../../components/status-message/status-message.module';
import { InstructorCourseEnrollPageComponent } from './instructor-course-enroll-page.component';

/**
 * Module for instructor course enroll page.
 */
@NgModule({
  declarations: [
    InstructorCourseEnrollPageComponent,
  ],
  exports: [
    InstructorCourseEnrollPageComponent,
  ],
  imports: [
    CommonModule,
    NgbModule,
    HotTableModule,
    StatusMessageModule,
    AjaxPreloadModule,
  ],
})
export class InstructorCourseEnrollPageModule { }
