import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';

import { InstructorHelpPageComponent } from './instructor-help-page.component';

import { InstructorHelpSectionComponent } from './instructor-help-section/instructor-help-section.component';
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
    RouterModule
  ],
  declarations: [
    InstructorHelpPageComponent,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpSectionComponent,
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
