import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
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
    InstructorCoursesPageComponent,
],
})
export class InstructorCoursesPageModule { }
