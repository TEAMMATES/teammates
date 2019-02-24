import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StudentListModule } from '../student-list/student-list.module';
import { InstructorSearchPageComponent } from './instructor-search-page.component';

/**
 * Module for instructor search page.
 */
@NgModule({
  declarations: [
    InstructorSearchPageComponent,
  ],
  exports: [
    InstructorSearchPageComponent,
  ],
  imports: [
    CommonModule,
    StudentListModule,
    FormsModule,
    RouterModule,
  ],
})
export class InstructorSearchPageModule { }
