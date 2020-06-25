import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentListModule } from '../../components/student-list/student-list.module';
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
    FormsModule,
    RouterModule,
    StudentListModule,
    NgbModule,
  ],
})
export class InstructorStudentListPageModule { }
