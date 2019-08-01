import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AddCourseFormComponent } from './add-course-form/add-course-form.component';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';

/**
 * Module for instructor courses page.
 */
@NgModule({
  declarations: [
    AddCourseFormComponent,
    InstructorCoursesPageComponent,
  ],
  exports: [
    InstructorCoursesPageComponent,
    AddCourseFormComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    NgbModule,
  ],
})
export class InstructorCoursesPageModule { }
