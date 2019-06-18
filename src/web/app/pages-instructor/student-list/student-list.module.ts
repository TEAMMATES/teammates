import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentListComponent } from './student-list.component';
import { JoinStatePipe } from './join-state.pipe';

/**
 * Module for student list table component.
 */
@NgModule({
  declarations: [
    StudentListComponent,
    JoinStatePipe,
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
