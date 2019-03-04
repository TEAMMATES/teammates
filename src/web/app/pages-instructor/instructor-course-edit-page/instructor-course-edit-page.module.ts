import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { CourseEditFormComponent } from './course-edit-form/course-edit-form.component';
import { DeleteInstructorModalComponent } from './delete-instructor-modal/delete-instructor-modal.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import { InstructorEditFormComponent } from './instructor-edit-form/instructor-edit-form.component';
import {
  InstructorSectionPrivilegesFormFormComponent,
} from './instructor-edit-form/instructor-section-privileges-form/instructor-section-privileges-form.component';
import { ResendReminderModalComponent } from './resend-reminder-modal/resend-reminder-modal.component';
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
    RouterModule,
    TeammatesCommonModule,
  ],
  declarations: [
    CourseEditFormComponent,
    DeleteInstructorModalComponent,
    InstructorCourseEditPageComponent,
    InstructorEditFormComponent,
    InstructorSectionPrivilegesFormFormComponent,
    ResendReminderModalComponent,
    ViewPrivilegesModalComponent,
  ],
  exports: [
    InstructorCourseEditPageComponent,
  ],
  entryComponents: [
    DeleteInstructorModalComponent,
    ResendReminderModalComponent,
    ViewPrivilegesModalComponent,
  ],
})
export class InstructorCourseEditPageModule { }
