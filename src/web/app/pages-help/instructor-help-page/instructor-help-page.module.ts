import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { InstructorHelpPageComponent } from './instructor-help-page.component';

import {
  InstructorHelpCoursesSectionComponent,
} from './instructor-help-courses-section/instructor-help-courses-section.component';
import {
  InstructorHelpStudentsSectionComponent,
} from './instructor-help-students-section/instructor-help-students-section.component';

/**
 * Module for instructor help page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    RouterModule,
  ],
  declarations: [
    InstructorHelpPageComponent,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpCoursesSectionComponent,
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
