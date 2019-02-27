import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StudentListModule } from '../student-list/student-list.module';
import { InstructorSearchPageComponent } from './instructor-search-page.component';
import { InstructorSearchBarComponent } from './instructor-search-bar/instructor-search-bar.component';

/**
 * Module for instructor search page.
 */
@NgModule({
  declarations: [
    InstructorSearchPageComponent,
    InstructorSearchBarComponent,
  ],
  exports: [
    InstructorSearchPageComponent,
    InstructorSearchBarComponent,
  ],
  imports: [
    CommonModule,
    StudentListModule,
    FormsModule,
    RouterModule,
  ],
})
export class InstructorSearchPageModule { }
