import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InstructorStudentListPageComponent } from './instructor-student-list-page.component';

/**
 * Module for instructor student list page.
 */
@NgModule({
  declarations: [
    InstructorStudentListPageComponent,
  ],
  exports: [
    InstructorStudentListPageComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class InstructorStudentListPageModule { }
