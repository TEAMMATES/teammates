import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { CourseEditFormModule } from './course-edit-form/course-edit-form.module';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import { ViewPrivilegesModalComponent } from './view-privileges-modal/view-privileges-modal.component';

/**
 * Module for instructor course edit page.
 */
@NgModule({
  imports: [
    AjaxLoadingModule,
    CommonModule,
    FormsModule,
    NgbModule,
    ReactiveFormsModule,
    TeammatesCommonModule,
    CourseEditFormModule,
  ],
  declarations: [
    InstructorCourseEditPageComponent,
    ViewPrivilegesModalComponent,
  ],
  exports: [
    InstructorCourseEditPageComponent,
  ],
  entryComponents: [
    ViewPrivilegesModalComponent,
  ],
})
export class InstructorCourseEditPageModule { }
