import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorStudentListPageComponent } from './instructor-student-list-page.component';



import { StudentListModule } from '../../components/student-list/student-list.module';


const routes: Routes = [
  {
    path: '',
    component: InstructorStudentListPageComponent,
  },
];

/**
 * Module for instructor student list page.
 */
@NgModule({
  exports: [
    InstructorStudentListPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    StudentListModule,
    NgbCollapseModule,
    InstructorStudentListPageComponent,
],
})
export class InstructorStudentListPageModule { }
