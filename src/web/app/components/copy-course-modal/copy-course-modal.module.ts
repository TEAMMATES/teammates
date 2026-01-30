import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { CopyCourseModalComponent } from './copy-course-modal.component';

/**
 * Module for copy current course modal.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    CopyCourseModalComponent,
  ],
  exports: [
    CopyCourseModalComponent,
  ],
})
export class CopyCourseModalModule { }
