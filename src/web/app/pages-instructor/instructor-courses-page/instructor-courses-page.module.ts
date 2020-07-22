import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { AddCourseFormModule } from './add-course-form/add-course-form.module';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorCoursesPageComponent,
  },
];

/**
 * Module for instructor courses page.
 */
@NgModule({
  declarations: [
    InstructorCoursesPageComponent,
  ],
  exports: [
    InstructorCoursesPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    NgbDropdownModule,
    NgbTooltipModule,
    AddCourseFormModule,
    LoadingSpinnerModule,
  ],
})
export class InstructorCoursesPageModule { }
