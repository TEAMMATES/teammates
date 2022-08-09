import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { CourseEditFormComponent } from './course-edit-form.component';

@NgModule({
  declarations: [
    CourseEditFormComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    AjaxLoadingModule,
    NgbTooltipModule,
    LoadingRetryModule,
    LoadingSpinnerModule,
  ],
  exports: [
    CourseEditFormComponent,
  ],
})
export class CourseEditFormModule { }
