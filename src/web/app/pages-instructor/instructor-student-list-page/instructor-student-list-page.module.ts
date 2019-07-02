import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { StudentListModule } from '../student-list/student-list.module';
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
    LoadingSpinnerModule,
    CommonModule,
    FormsModule,
    RouterModule,
    StudentListModule,
  ],
})
export class InstructorStudentListPageModule { }
