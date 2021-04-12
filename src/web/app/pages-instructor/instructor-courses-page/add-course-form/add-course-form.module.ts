import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { AddCourseFormComponent } from './add-course-form.component';

/**
 * Module for form for adding courses.
 */
@NgModule({
  declarations: [AddCourseFormComponent],
  exports: [AddCourseFormComponent],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    AjaxLoadingModule,
    NgbTooltipModule,
  ],
})
export class AddCourseFormModule { }
