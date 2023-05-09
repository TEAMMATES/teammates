import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CopyCourseModalModule } from '../../components/copy-course-modal/copy-course-modal.module';
import { CourseEditFormModule } from '../../components/course-edit-form/course-edit-form.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  ModifiedTimestampModalModule,
} from '../../components/modified-timestamps-modal/modified-timestamps-module.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { ProgressBarModule } from '../../components/progress-bar/progress-bar.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorCoursesPageComponent,
  },
];

/**
 * Module for instructor courses page.
 */
@NgModule({
  declarations: [
    InstructorCoursesPageComponent,
  ],
  exports: [
    InstructorCoursesPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    NgbDropdownModule,
    NgbTooltipModule,
    LoadingSpinnerModule,
    AjaxLoadingModule,
    LoadingRetryModule,
    PanelChevronModule,
    TeammatesRouterModule,
    CopyCourseModalModule,
    ProgressBarModule,
    CourseEditFormModule,
    ModifiedTimestampModalModule,
  ],
})
export class InstructorCoursesPageModule { }
