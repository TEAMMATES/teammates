import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentListComponent } from './student-list.component';

/**
 * Module for student list table component.
 */
@NgModule({
  declarations: [
    StudentListComponent,
  ],
  exports: [
    StudentListComponent,
  ],
  imports: [
    CommonModule,
    NgbModule,
    RouterModule,
  ],
})
export class StudentListModule { }
