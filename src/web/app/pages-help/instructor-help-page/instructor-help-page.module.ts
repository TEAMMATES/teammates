import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { InstructorHelpPageComponent } from './instructor-help-page.component';
import { ScrollSpyDirective } from './scroll-spy.directive';

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
    ScrollSpyDirective,
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
