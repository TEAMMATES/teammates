import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentProfileModule } from '../../pages-instructor/student-profile/student-profile.module';

import { InstructorHelpPageComponent } from './instructor-help-page.component';

import {
  InstructorCourseStudentEditPageModule,
} from '../../pages-instructor/instructor-course-student-edit-page/instructor-course-student-edit-page.module';
import {
  InstructorCoursesPageModule,
} from '../../pages-instructor/instructor-courses-page/instructor-courses-page.module';
import {
  InstructorSearchPageModule,
} from '../../pages-instructor/instructor-search-page/instructor-search-page.module';
import { ExampleBoxComponent } from './example-box/example-box.component';
import {
  InstructorHelpCoursesSectionComponent,
} from './instructor-help-courses-section/instructor-help-courses-section.component';
import {
  InstructorHelpGettingStartedComponent,
} from './instructor-help-getting-started/instructor-help-getting-started.component';
import {
  InstructorHelpQuestionsSectionComponent,
} from './instructor-help-questions-section/instructor-help-questions-section.component';
import {
  InstructorHelpSessionsSectionComponent,
} from './instructor-help-sessions-section/instructor-help-sessions-section.component';
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
    StudentProfileModule,
    ReactiveFormsModule,
    InstructorSearchPageModule,
    InstructorCourseStudentEditPageModule,
    InstructorCoursesPageModule,
  ],
  declarations: [
    InstructorHelpPageComponent,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpSessionsSectionComponent,
    InstructorHelpQuestionsSectionComponent,
    InstructorHelpCoursesSectionComponent,
    InstructorHelpGettingStartedComponent,
    ExampleBoxComponent,
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
